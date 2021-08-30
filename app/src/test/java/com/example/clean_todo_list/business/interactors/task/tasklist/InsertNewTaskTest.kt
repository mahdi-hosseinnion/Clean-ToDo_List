package com.example.clean_todo_list.business.interactors.task.tasklist

import com.example.clean_todo_list.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.example.clean_todo_list.business.data.cache.FORCE_EXCEPTION
import com.example.clean_todo_list.business.data.cache.FORCE_GENERAL_FAILURE
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.task.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.interactors.task.tasklist.InsertNewTask.Companion.INSERT_TASK_FAILED
import com.example.clean_todo_list.business.interactors.task.tasklist.InsertNewTask.Companion.INSERT_TASK_SUCCESS
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.framework.presentation.task.tasklist.state.TaskListStateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

/*
    test cases:
    1. insertTask_success_confirmCacheAndNetworkChanged
        a) insert new task
        b) observe for INSERT_NEW_TASK_SUCCESS from flow
        c) confirm cache was updated
        d) confirm network was updated
    2. insertAsk_fail_confirmCacheAndNetworkDidNotChange
        a) insert new task
        b) force failure (return -1 from database)
        c) observe for INSERT_NEW_TASK_FAIL from flow
        d) confirm cache was not updated
        e) confirm network was not updated
    3. throwException_checkForError_confirmCacheAndNetworkDidNotChange
        a) insert new task
        b) force exception (throw exception in database)
        c) observe for CACHE_ERROR_UNKNOWN from flow
        d) confirm cache was not updated
        e) confirm network was not updated
 */
class InsertNewTaskTest {

    //system under test
    private val insertNewTask: InsertNewTask

    // dependencies
    private val dependencyContainer = DependencyContainer()
    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    private val taskNetworkDataSource: TaskNetworkDataSource =
        dependencyContainer.taskNetworkDataSource

    init {
        insertNewTask = InsertNewTask(
            taskCacheDataSource = taskCacheDataSource,
            taskNetworkDataSource = taskNetworkDataSource
        )
    }

    @Test
    fun insertTask_success_confirmCacheAndNetworkChanged() = runBlocking {
        val newTask = TaskFactory.createTask(title = UUID.randomUUID().toString())

        insertNewTask.insertTaskTask(
            id = newTask.id,
            title = newTask.title,
            stateEvent = TaskListStateEvent.InsertNewTaskEvent("")
        ).collect {
            assertEquals(
                INSERT_TASK_SUCCESS,
                it?.stateMessage?.response?.message
            )
        }
        //confirm cache was updated
        val cacheTaskThatWasInserted = taskCacheDataSource.searchTaskById(newTask.id)
        assertEquals(newTask, cacheTaskThatWasInserted)

        //confirm network was updated
        val networkTaskThatWasInserted = taskNetworkDataSource.searchTask(newTask)
        assertEquals(newTask, networkTaskThatWasInserted)
    }

    @Test
    fun insertAsk_fail_confirmCacheAndNetworkDidNotChange() = runBlocking {
        val newTask = TaskFactory.createTask(
            id = FORCE_GENERAL_FAILURE,
            title = UUID.randomUUID().toString()
        )

        insertNewTask.insertTaskTask(
            id = newTask.id,
            title = newTask.title,
            stateEvent = TaskListStateEvent.InsertNewTaskEvent("")
        ).collect {
            assertEquals(
                INSERT_TASK_FAILED,
                it?.stateMessage?.response?.message
            )
        }
        //confirm cache was not updated
        val cacheTaskThatWasInserted = taskCacheDataSource.searchTaskById(newTask.id)
        assertEquals(null, cacheTaskThatWasInserted)

        //confirm network was not updated
        val networkTaskThatWasInserted = taskNetworkDataSource.searchTask(newTask)
        assertEquals(null, networkTaskThatWasInserted)
    }

    @Test
    fun throwException_checkForError_confirmCacheAndNetworkDidNotChange() = runBlocking {
        val newTask = TaskFactory.createTask(
            id = FORCE_EXCEPTION,
            title = UUID.randomUUID().toString()
        )

        insertNewTask.insertTaskTask(
            id = newTask.id,
            title = newTask.title,
            stateEvent = TaskListStateEvent.InsertNewTaskEvent("")
        ).collect {
            assert(
                (it?.stateMessage?.response?.message)
                    ?.contains(CACHE_ERROR_UNKNOWN) ?: false
            )
        }
        //confirm cache was not updated
        val cacheTaskThatWasInserted = taskCacheDataSource.searchTaskById(newTask.id)
        assertEquals(null, cacheTaskThatWasInserted)

        //confirm network was not updated
        val networkTaskThatWasInserted = taskNetworkDataSource.searchTask(newTask)
        assertEquals(null, networkTaskThatWasInserted)
    }
}