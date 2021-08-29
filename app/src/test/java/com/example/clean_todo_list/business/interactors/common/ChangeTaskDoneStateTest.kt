package com.example.clean_todo_list.business.interactors.common

import com.example.clean_todo_list.business.data.cache.CacheErrors
import com.example.clean_todo_list.business.data.cache.FORCE_EXCEPTION
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.domain.state.ViewState
import com.example.clean_todo_list.business.interactors.common.ChangeTaskDoneState.Companion.UPDATE_TASK_DONE_STATE_FAILED
import com.example.clean_todo_list.business.interactors.common.ChangeTaskDoneState.Companion.UPDATE_TASK_DONE_STATE_SUCCESS
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.framework.presentation.task.tasklist.state.TaskListStateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * 1. changeDoneState_success_confirmDoneStateUpdatedInCacheAndNetwork()
 *      a) select random task from cache
 *      b) reverse isDone value
 *      c) perform change task done state
 *      d) observe for UPDATE_TASK_DONE_STATE_SUCCESS emitted by flow
 *      e) confirm task updated in cache
 *      f) confirm task updated in network
 * 2. changeDoneState_fail_confirmDoneStateDidNotUpdateInCacheAndNetwork()
 *      a) try to change done state of random task(fail b/c it's not in database)
 *      b) observe for UPDATE_TASK_DONE_STATE_FAILED emitted by flow
 *      c) confirm task DID NOT update in cache
 *      d) confirm task DID NOT  update in network
 * 3. forceException_genericError_confirmDoneStateDidNotUpdateInCacheAndNetwork()
 *      a) select random task from cache (change isDone)
 *      b) force exception
 *      c) observe for CACHE_ERROR_UNKNOWN emitted by flow
 *      d) confirm task DID NOT update in cache
 *      e) confirm task DID NOT  update in network
 */
class ChangeTaskDoneStateTest {

    //system under test
    private val changeTaskDoneState: ChangeTaskDoneState<ViewState>

    // dependencies
    private val dependencyContainer = DependencyContainer()
    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    private val taskNetworkDataSource: TaskNetworkDataSource =
        dependencyContainer.taskNetworkDataSource

    init {
        changeTaskDoneState = ChangeTaskDoneState(
            taskCacheDataSource = taskCacheDataSource,
            taskNetworkDataSource = taskNetworkDataSource
        )
    }


    @Test
    fun changeDoneState_success_confirmDoneStateUpdatedInCacheAndNetwork() = runBlocking {

        val allTaskInCache = taskCacheDataSource.getAllTasks()
        val randomTask = allTaskInCache.random()

        val taskToUpdate = if (randomTask.isDone) {
            randomTask.copy(isDone = false)
        } else {
            randomTask.copy(isDone = true)
        }
        //make sure isDone changed
        assertNotEquals(randomTask.isDone, taskToUpdate.isDone)

        changeTaskDoneState.changeTaskDoneState(
            taskToUpdate.id,
            taskToUpdate.isDone,
            stateEvent = TaskListStateEvent.ChangeTaskDoneStateEvent(
                taskToUpdate.id,
                taskToUpdate.isDone
            )
        ).collect {
            assertEquals(
                UPDATE_TASK_DONE_STATE_SUCCESS,
                it?.stateMessage?.response?.message
            )
        }

        //confirm cache updated
        assertEquals(
            taskToUpdate,
            taskCacheDataSource.searchTaskById(taskToUpdate.id)
        )
        //confirm network updated
        assertEquals(
            taskToUpdate,
            taskNetworkDataSource.searchTask(taskToUpdate)
        )
    }

    @Test
    fun changeDoneState_fail_confirmDoneStateDidNotUpdateInCacheAndNetwork() = runBlocking {

        val taskToUpdate = TaskFactory.createRandomTask()

        //make sure task is not in cache(false-positive)
        assertNull(taskCacheDataSource.searchTaskById(taskToUpdate.id))

        changeTaskDoneState.changeTaskDoneState(
            taskToUpdate.id,
            taskToUpdate.isDone,
            stateEvent = TaskListStateEvent.ChangeTaskDoneStateEvent(
                taskToUpdate.id,
                taskToUpdate.isDone
            )
        ).collect {
            assertEquals(
                UPDATE_TASK_DONE_STATE_FAILED,
                it?.stateMessage?.response?.message
            )
        }

        //confirm it did not inserted into cache
        assertNull(
            taskCacheDataSource.searchTaskById(taskToUpdate.id)
        )
        //confirm it did not inserted into network
        assertNull(
            taskNetworkDataSource.searchTask(taskToUpdate)
        )

    }

    @Test
    fun forceException_genericError_confirmDoneStateDidNotUpdateInCacheAndNetwork() = runBlocking {

        val allTaskInCache = taskCacheDataSource.getAllTasks()

        val randomTask = allTaskInCache.random()

        val taskToUpdate = if (randomTask.isDone) {
            randomTask.copy(
                id = FORCE_EXCEPTION,
                isDone = false
            )
        } else {
            randomTask.copy(
                id = FORCE_EXCEPTION,
                isDone = true
            )
        }

        changeTaskDoneState.changeTaskDoneState(
            taskToUpdate.id,
            taskToUpdate.isDone,
            stateEvent = TaskListStateEvent.ChangeTaskDoneStateEvent(
                taskToUpdate.id,
                taskToUpdate.isDone
            )
        ).collect {
            assert(
                it?.stateMessage?.response?.message?.contains(
                    CacheErrors.CACHE_ERROR_UNKNOWN
                )?:false
            )
        }

        //confirm cache did not updated
        assertEquals(
            randomTask,
            taskCacheDataSource.searchTaskById(randomTask.id)
        )
        //confirm network did not updated
        assertEquals(
            randomTask,
            taskNetworkDataSource.searchTask(randomTask)
        )
        //confirm it did not inserted into cache
        assertNull(
            taskCacheDataSource.searchTaskById(taskToUpdate.id)
        )
        //confirm it did not inserted into network
        assertNull(
            taskNetworkDataSource.searchTask(taskToUpdate)
        )
    }
}