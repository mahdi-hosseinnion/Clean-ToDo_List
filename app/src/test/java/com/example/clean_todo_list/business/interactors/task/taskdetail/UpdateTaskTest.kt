package com.example.clean_todo_list.business.interactors.task.taskdetail

import com.example.clean_todo_list.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.example.clean_todo_list.business.data.cache.FORCE_EXCEPTION
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.task.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.interactors.task.taskdetail.UpdateTask.Companion.UPDATE_TASK_FAILED
import com.example.clean_todo_list.business.interactors.task.taskdetail.UpdateTask.Companion.UPDATE_TASK_SUCCESS
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder
import com.example.clean_todo_list.framework.presentation.task.taskdetail.state.TaskDetailStateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

/*
    test cases:
    1. update_success_confirmCacheAndNetworkUpdated
        a) get existed task
        b) change body an title
        c) update that task
        d) observe for UPDATE_TASK_SUCCESS from flow
        e) confirm cache was updated
        f) confirm network was updated
    2. updateTask_fail_confirmCacheAndNetworkUnUpdated
        a) try to update random task
        d) observe for UPDATE_TASK_FAILED from flow
        e) confirm cache was not updated
        f) confirm network was not updated
    3. throwException_checkForError_confirmCacheAndNetworkUnUpdate
        a) update task )
        b) force exception (throw exception in database)
        c) observe for CACHE_ERROR_UNKNOWN from flow
        d) confirm cache was not updated
        e) confirm network was not updated
 */
class UpdateTaskTestTest {

    //system under test
    private val updateTask: UpdateTask

    // dependencies
    private val dependencyContainer = DependencyContainer()
    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    private val taskNetworkDataSource: TaskNetworkDataSource =
        dependencyContainer.taskNetworkDataSource

    init {
        updateTask = UpdateTask(
            taskCacheDataSource = taskCacheDataSource,
            taskNetworkDataSource = taskNetworkDataSource
        )
    }

    @Test
    fun update_success_confirmCacheAndNetworkUpdated() = runBlocking {
        val allTasksInCacheBeforeUpdate = taskCacheDataSource.searchTask(
            "", SortAndOrder.CREATED_DATE_ASC, 1
        )
        val taskToUpdate = allTasksInCacheBeforeUpdate.random().copy(
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )
        updateTask.updateTask(
            taskToUpdate,
            TaskDetailStateEvent.UpdateTaskEvent(taskToUpdate)
        ).collect {
            assertEquals(
                UPDATE_TASK_SUCCESS,
                it?.stateMessage?.response?.message
            )
        }
        //confirm cache was updated
        val cacheTask=taskCacheDataSource.searchTaskById(taskToUpdate.id)
        assertEquals(
            taskToUpdate.title,
            cacheTask?.title
        )
        assertEquals(
            taskToUpdate.body,
            cacheTask?.body
        )
        assertNotEquals(
            taskToUpdate.updated_at,
            cacheTask?.updated_at
        )
        //confirm network was updated
        val networkTask=taskNetworkDataSource.searchTask(taskToUpdate)
        assertEquals(
            taskToUpdate.title,
            networkTask?.title
        )
        assertEquals(
            taskToUpdate.body,
            networkTask?.body
        )
        assertNotEquals(
            taskToUpdate.updated_at,
            networkTask?.updated_at
        )
    }

    @Test
    fun updateTask_fail_confirmCacheAndNetworkUnUpdated() = runBlocking {

        val taskToUpdate = TaskFactory.createRandomTask()

        updateTask.updateTask(
            taskToUpdate,
            TaskDetailStateEvent.UpdateTaskEvent(taskToUpdate)
        ).collect {
            assertEquals(
                UPDATE_TASK_FAILED,
                it?.stateMessage?.response?.message
            )
        }
        //confirm cache was not updated
        assertNull(
            taskCacheDataSource.searchTaskById(taskToUpdate.id)
        )
        //confirm network was not updated
        assertNull(
            taskNetworkDataSource.searchTask(taskToUpdate)
        )
    }

    @Test
    fun throwException_checkForError_confirmCacheAndNetworkUnUpdate() = runBlocking {
        val taskToUpdate = TaskFactory.createRandomTask()
            .copy(
                id = FORCE_EXCEPTION
            )

        updateTask.updateTask(
            taskToUpdate,
            TaskDetailStateEvent.UpdateTaskEvent(taskToUpdate)
        ).collect {
            assert(

                it?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN) ?: false
            )
        }
        //confirm cache was not updated
        assertNull(
            taskCacheDataSource.searchTaskById(taskToUpdate.id)
        )
        //confirm network was not updated
        assertNull(
            taskNetworkDataSource.searchTask(taskToUpdate)
        )
    }
}