package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.di.DependencyContainer


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

}