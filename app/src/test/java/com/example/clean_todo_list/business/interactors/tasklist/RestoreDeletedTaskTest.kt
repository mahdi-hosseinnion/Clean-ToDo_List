package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.example.clean_todo_list.business.data.cache.FORCE_EXCEPTION
import com.example.clean_todo_list.business.data.cache.FORCE_GENERAL_FAILURE
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.interactors.tasklist.RestoreDeletedTask.Companion.RESTORE_TASK_FAILED
import com.example.clean_todo_list.business.interactors.tasklist.RestoreDeletedTask.Companion.RESTORE_TASK_SUCCESS
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


/**
 * 1. restoreTask_success_confirmCacheAndNetworkUpdated()
 *      a)insert random task into 'deleted' node (simulate deleted task state)
 *      b)restore that task
 *      c)observe for RESTORE_TASK_SUCCESS emitted by flow
 *      d)confirm task inserted into cache
 *      e)confirm task inserted into 'tasks' node in network
 *      f)confirm task deleted from 'deleted' node in network
 *
 * 2. restoreTask_fail_confirmCacheAndNetworkUnUpdated()
 *      a)insert random task into 'deleted' node (simulate deleted task state)
 *      b)restore that task(failure while inserting)
 *      c)observe for RESTORE_TASK_FAILED emitted by flow
 *      d)confirm task is NOT in cache
 *      e)confirm task is NOT in 'tasks' node in network
 *      f)confirm task is in 'deleted' node in network
 * 3. throwException_generalError_confirmCacheAndNetworkUnUpdated()
 *      a)insert random task into 'deleted' node (simulate deleted task state)
 *      b)restore task(force exception to throw)
 *      c)observe for CACHE_ERROR_UNKNOWN emitted by flow
 *      d)confirm task is NOT in cache
 *      e)confirm task is NOT in 'tasks' node in network
 *      f)confirm task is in 'deleted' node in network
 */

class RestoreDeletedTaskTest {
    //system under test
    private val restoreDeletedTask: RestoreDeletedTask

    // dependencies
    private val dependencyContainer = DependencyContainer()
    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    private val taskNetworkDataSource: TaskNetworkDataSource =
        dependencyContainer.taskNetworkDataSource

    init {
        restoreDeletedTask = RestoreDeletedTask(
            taskCacheDataSource = taskCacheDataSource,
            taskNetworkDataSource = taskNetworkDataSource
        )
    }

    @Test
    fun restoreTask_success_confirmCacheAndNetworkUpdated() = runBlocking {
        val deletedTask = TaskFactory.createRandomTask()
        taskNetworkDataSource.insertDeletedTask(deletedTask)
        //confirm there is task in 'deleted node'
        val deletedTasksBeforeRestore = taskNetworkDataSource.getDeletedTasks()
        assertTrue {
            deletedTasksBeforeRestore.contains(deletedTask)
        }
        //restore
        restoreDeletedTask.restoreDeletedTask(
            deletedTask,
            TaskListStateEvent.RestoreDeletedTaskEvent(deletedTask)
        ).collect {
            assertEquals(
                RESTORE_TASK_SUCCESS,
                it?.stateMessage?.response?.message
            )
        }
        //confirm inserted in cache
        assertEquals(
            deletedTask,
            taskCacheDataSource.searchTaskById(deletedTask.id)
        )
        //confirm inserted in 'tasks' node in network
        assertEquals(
            deletedTask,
            taskNetworkDataSource.searchTask(deletedTask)
        )
        //confirm does not exist in 'deleted' node in network
        val deletedTasks = taskNetworkDataSource.getDeletedTasks()
        assertFalse {
            deletedTasks.contains(deletedTask)
        }

    }

    @Test
    fun restoreTask_fail_confirmCacheAndNetworkUnUpdated() = runBlocking {

        val deletedTask = TaskFactory.createRandomTask().copy(id = FORCE_GENERAL_FAILURE)

        taskNetworkDataSource.insertDeletedTask(deletedTask)
        //confirm there is task in 'deleted node'
        val deletedTasksBeforeRestore = taskNetworkDataSource.getDeletedTasks()
        assertTrue {
            deletedTasksBeforeRestore.contains(deletedTask)
        }
        //restore
        restoreDeletedTask.restoreDeletedTask(
            deletedTask,
            TaskListStateEvent.RestoreDeletedTaskEvent(deletedTask)
        ).collect {
            assertEquals(
                RESTORE_TASK_FAILED,
                it?.stateMessage?.response?.message
            )
        }
        //confirm is not in cache
        assertNull(
            taskCacheDataSource.searchTaskById(deletedTask.id)
        )
        //confirm is not in 'tasks' node in network
        assertNull(
            taskNetworkDataSource.searchTask(deletedTask)
        )
        //confirm does not exist in 'deleted' node in network
        val deletedTasks = taskNetworkDataSource.getDeletedTasks()
        assertTrue {
            deletedTasks.contains(deletedTask)
        }
    }

    @Test
    fun throwException_generalError_confirmCacheAndNetworkUnUpdated() = runBlocking {

        val deletedTask = TaskFactory.createRandomTask().copy(id = FORCE_EXCEPTION)

        taskNetworkDataSource.insertDeletedTask(deletedTask)
        //confirm there is task in 'deleted node'
        val deletedTasksBeforeRestore = taskNetworkDataSource.getDeletedTasks()
        assertTrue {
            deletedTasksBeforeRestore.contains(deletedTask)
        }
        //restore
        restoreDeletedTask.restoreDeletedTask(
            deletedTask,
            TaskListStateEvent.RestoreDeletedTaskEvent(deletedTask)
        ).collect {
            assert(
                it?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN) ?: false
            )
        }
        //confirm is not in cache
        assertNull(
            taskCacheDataSource.searchTaskById(deletedTask.id)
        )
        //confirm is not in 'tasks' node in network
        assertNull(
            taskNetworkDataSource.searchTask(deletedTask)
        )
        //confirm does not exist in 'deleted' node in network
        val deletedTasks = taskNetworkDataSource.getDeletedTasks()
        assertTrue {
            deletedTasks.contains(deletedTask)
        }
    }


}