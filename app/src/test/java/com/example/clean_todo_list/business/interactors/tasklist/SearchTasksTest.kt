package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.example.clean_todo_list.business.data.cache.FORCE_EXCEPTION
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.interactors.tasklist.SearchTasks.Companion.SEARCH_TASKS_NO_MATCHING_RESULTS
import com.example.clean_todo_list.business.interactors.tasklist.SearchTasks.Companion.SEARCH_TASKS_SUCCESS
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


/**
 * test cases:
 * 1. blankQuery_success_confirmAllTaskReceived
 *  a)search blank query
 *  b)observer for SEARCH_TASKS_SUCCESS emitted from flow
 *  c)confirm task were received
 *  d)confirm received task are same as database task
 *
 * 2. randomQuery_success_confirmNoMatchingResult
 *  a)search with unmatchable query(long and random)
 *  b)observer for SEARCH_TASKS_NO_MATCHING_RESULTS emitted from flow
 *  c)confirm no task received
 *  d)confirm there is task in cache
 *
 * 3. forceException_fail_confirmNoTaskReceived
 *  a)search tasks
 *  b)force exception
 *  c)observer for CACHE_ERROR_UNKNOWN emitted from flow
 *  d)confirm no task received
 *  e)confirm there is task in cache
 */
class SearchTasksTest {
    //system under test
    private val searchTasks: SearchTasks

    // dependencies
    private val dependencyContainer = DependencyContainer()

    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    private val taskNetworkDataSource: TaskNetworkDataSource =
        dependencyContainer.taskNetworkDataSource

    init {
        searchTasks = SearchTasks(
            taskCacheDataSource = taskCacheDataSource
        )
    }

    private val filterAndOrder = SortAndOrder.CREATED_DATE_ASC

    @Test
    fun blankQuery_success_confirmAllTaskReceived() = runBlocking {
        val query = ""
        var result: ArrayList<Task>? = null

        searchTasks.searchTasks(
            query = query,
            sortAndOrder = filterAndOrder,
            page = 1,
            stateEvent = TaskListStateEvent.SearchTasksEvent()
        ).collect { dataState ->
            assertEquals(
                SEARCH_TASKS_SUCCESS,
                dataState?.stateMessage?.response?.message
            )
            result = dataState?.data?.taskList
        }
        // confirm tasks were retrieved
        assertTrue { result != null }
        //check for data
        val allTasksInCache = taskCacheDataSource.searchTask(query, filterAndOrder, 1)
        assertEquals(allTasksInCache, result)
    }

    @Test
    fun randomQuery_success_confirmNoMatchingResult() = runBlocking {
        val query = "lasjfdk2947324jh2kl41u9471984kj4kl2j3kl;523524324jlkjlkasdjr324"
        var result: ArrayList<Task>? = null

        searchTasks.searchTasks(
            query = query,
            sortAndOrder = filterAndOrder,
            page = 1,
            stateEvent = TaskListStateEvent.SearchTasksEvent()
        ).collect { dataState ->
            assertEquals(
                SEARCH_TASKS_NO_MATCHING_RESULTS,
                dataState?.stateMessage?.response?.message
            )
            result = dataState?.data?.taskList
        }
        // confirm tasks were not retrieved
        assertTrue { result?.isEmpty() ?: false }
        //check if there is data in cache
        val allTasksInCache = taskCacheDataSource.searchTask("", filterAndOrder, 1)
        assertTrue { allTasksInCache.isNotEmpty() }
    }

    @Test
    fun forceException_fail_confirmNoTaskReceived() = runBlocking {
        val query = FORCE_EXCEPTION
        var result: ArrayList<Task>? = null

        searchTasks.searchTasks(
            query = query,
            sortAndOrder = filterAndOrder,
            page = 1,
            stateEvent = TaskListStateEvent.SearchTasksEvent()
        ).collect { dataState ->
            assertTrue {
                dataState?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN) ?: false
            }

            result = dataState?.data?.taskList
        }
        // confirm tasks were not retrieved
        assertTrue { result.isNullOrEmpty() }
        //check if there is data in cache
        val allTasksInCache = taskCacheDataSource.searchTask("", filterAndOrder, 1)
        assertTrue { allTasksInCache.isNotEmpty() }
    }
}