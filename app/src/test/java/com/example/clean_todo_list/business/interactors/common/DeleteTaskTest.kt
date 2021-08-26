package com.example.clean_todo_list.business.interactors.common

import com.example.clean_todo_list.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.example.clean_todo_list.business.data.cache.FORCE_EXCEPTION
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.interactors.common.DeleteTask.Companion.DELETE_TASK_FAILED
import com.example.clean_todo_list.business.interactors.common.DeleteTask.Companion.DELETE_TASK_SUCCESS
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


/**
 * 1. deleteTask_success_confirmDeletedInCacheAndNetworkAndAddedToDeletedTask
 *  a) delete a task
 *  b) observer for DELETE_TASK_SUCCESS emitted by flow
 *  c) confirm task deleted in cache
 *  d) confirm task deleted in network
 *  e) confirm task added to deleted task node
 * 2. deleteTask_fail_confirmDidNotDeletedInCacheAndNetworkAndDidNotAddToDeletedTask
 *  a) delete a task that does no exist in database(fail)
 *  b) observer for DELETE_TASK_FAILED emitted by flow
 *  c) confirm task did not delete in cache
 *  d) confirm task did not delete in network
 *  e) confirm task did not add to deleted task node
 * 3. forceException_fail_confirmDidNotDeletedInCacheAndNetworkAndDidNotAddToDeletedTask
 *  a) for exception on delete task
 *  b) observer for CACHE_ERROR_UNKNOWN emitted by flow
 *  c) confirm task did not delete in cache
 *  d) confirm task did not delete in network
 *  e) confirm task did not add to deleted task node
 */

class DeleteTaskTest {
    //system under test
    private val deleteTask: DeleteTask<TaskListViewState>

    // dependencies
    private val dependencyContainer = DependencyContainer()
    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    private val taskNetworkDataSource: TaskNetworkDataSource =
        dependencyContainer.taskNetworkDataSource

    init {
        deleteTask = DeleteTask(
            taskCacheDataSource = taskCacheDataSource,
            taskNetworkDataSource = taskNetworkDataSource
        )
    }

    @Test
    fun deleteTask_success_confirmDeletedInCacheAndNetworkAndAddedToDeletedTask() = runBlocking {

        val allTasksInCache = taskCacheDataSource.searchTask("", SortAndOrder.CREATED_DATE_ASC, 1)
        //select random task to delete
        val taskToDelete = allTasksInCache.random()

        deleteTask.deleteTask(taskToDelete, TaskListStateEvent.DeleteTaskEvent(taskToDelete))
            .collect {
                assertEquals(
                    DELETE_TASK_SUCCESS,
                    it?.stateMessage?.response?.message
                )
            }

        //confirm note deleted in cache
        val allTasksInCacheAfterDelete = taskCacheDataSource
            .searchTask("", SortAndOrder.CREATED_DATE_ASC, 1)
        assertFalse { allTasksInCacheAfterDelete.contains(taskToDelete) }

        //confirm note deleted in 'tasks' node
        val allTasksInNetworkAfterDelete = taskNetworkDataSource
            .getAllTasks()
        assertFalse { allTasksInNetworkAfterDelete.contains(taskToDelete) }

        //confirm add to 'deletedTasks' node
        val deletedTasks = taskNetworkDataSource
            .getDeletedTasks()
        assertTrue { deletedTasks.contains(taskToDelete) }

    }

    @Test
    fun deleteTask_fail_confirmCacheAndNetworkUnChange() = runBlocking {
        val allTasksInCache = taskCacheDataSource.searchTask("", SortAndOrder.CREATED_DATE_ASC, 1)
        val allTasksInNetwork = taskNetworkDataSource.getAllTasks()
        //select random task to delete
        val taskToDelete = TaskFactory.createRandomTask()

        deleteTask.deleteTask(taskToDelete, TaskListStateEvent.DeleteTaskEvent(taskToDelete))
            .collect {
                assertEquals(
                    DELETE_TASK_FAILED,
                    it?.stateMessage?.response?.message
                )
            }

        val numOfTaskInCacheAfterDelete = taskCacheDataSource
            .getNumOfTasks()
        val allTasksInNetworkAfterDelete = taskNetworkDataSource.getAllTasks()
        val allTasksInDeletedTasksAfterDelete = taskNetworkDataSource.getDeletedTasks()
        //confirm cache did not change
        assertEquals(allTasksInCache.size, numOfTaskInCacheAfterDelete)
        //confirm 'task' node in network does not changed
        assertEquals(allTasksInNetwork, allTasksInNetworkAfterDelete)
        //confirm task did not added to 'deletedTask" node
        assertFalse { allTasksInDeletedTasksAfterDelete.contains(taskToDelete) }


    }

    @Test
    fun forceException_fail_confirmCacheAndNetworkUnChange() = runBlocking {
        val allTasksInCache = taskCacheDataSource.searchTask("", SortAndOrder.CREATED_DATE_ASC, 1)
        val allTasksInNetwork = taskNetworkDataSource.getAllTasks()
        //select random task to delete
        val taskToDelete = TaskFactory.createTask(
            title = "",
            id = FORCE_EXCEPTION
        )

        deleteTask.deleteTask(taskToDelete, TaskListStateEvent.DeleteTaskEvent(taskToDelete))
            .collect {
                assertTrue {
                    it?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN) ?: false
                }
            }
        val numOfTaskInCacheAfterDelete = taskCacheDataSource
            .getNumOfTasks()
        val allTasksInNetworkAfterDelete = taskNetworkDataSource.getAllTasks()
        val allTasksInDeletedTasksAfterDelete = taskNetworkDataSource.getDeletedTasks()

        //confirm cache did not change
        assertEquals(allTasksInCache.size, numOfTaskInCacheAfterDelete)
        //confirm 'task' node in network does not changed
        assertEquals(allTasksInNetwork, allTasksInNetworkAfterDelete)
        //confirm task did not added to 'deletedTask" node
        assertFalse { allTasksInDeletedTasksAfterDelete.contains(taskToDelete) }
    }
}


