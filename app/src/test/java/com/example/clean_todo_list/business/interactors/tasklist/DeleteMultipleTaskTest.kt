package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.data.cache.FORCE_EXCEPTION
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.interactors.tasklist.DeleteMultipleTask.Companion.DELETE_TASKS_ERRORS
import com.example.clean_todo_list.business.interactors.tasklist.DeleteMultipleTask.Companion.DELETE_TASKS_SUCCESS
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.framework.datasource.cache.util.FilterAndOrder
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.collections.ArrayList

/**
 * 1. deleteTasks_success_confirmCacheAndNetworkUpdated
 *      a) select some random tasks from database and delete them
 *      b) observe for DELETE_TASKS_SUCCESS emitted from flow
 *      c) confirm tasks were deleted form cache
 *      d) confirm tasks were deleted form 'tasks' node
 *      e) confirm tasks were inserted to 'deleted' node
 * 2. deleteTasks_fail_confirmCacheAndNetworkUpdatedViaSuccessfulOnes
 *     - This is a complex one:
 *      - The use-case will attempt to delete all tasks passed as input. If there
 *      is an error with a particular delete, it continues with the others. But the
 *      resulting msg is DELETE_TASKS_ERRORS. So we need to do rigorous checks here
 *      to make sure the correct tasks were deleted and the correct tasks were not.
 *
 *      a) select a handful of random tasks for deleting
 *      b) change the ids of a few tasks so they will cause errors when deleting
 *      c) confirm DELETE_TASKS_ERRORS msg is emitted from flow
 *      d) confirm ONLY the valid tasks are deleted from network "tasks" node
 *      e) confirm ONLY the valid tasks are inserted into network "deletes" node
 *      f) confirm ONLY the valid tasks are deleted from cache
 * 3. forceException_fail_confirmCacheAndNetworkUnUpdated
 *      a) select a handful of random tasks for deleting
 *      b) force exception to throw on handful of them
 *      c) confirm DELETE_TASKS_ERRORS msg is emitted from flow
 *      d) confirm ONLY the valid tasks are deleted from network "tasks" node
 *      e) confirm ONLY the valid tasks are inserted into network "deletes" node
 *      f) confirm ONLY the valid tasks are deleted from cache
 */
class DeleteMultipleTaskTest {

    //system under test
    private val deleteMultipleTask: DeleteMultipleTask

    // dependencies
    private val dependencyContainer = DependencyContainer()
    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    private val taskNetworkDataSource: TaskNetworkDataSource =
        dependencyContainer.taskNetworkDataSource

    init {
        deleteMultipleTask = DeleteMultipleTask(
            taskCacheDataSource = taskCacheDataSource,
            taskNetworkDataSource = taskNetworkDataSource
        )
    }

    @Test
    fun deleteTasks_success_confirmCacheAndNetworkUpdated() = runBlocking {
        val tasksInCache = taskCacheDataSource.searchTask("", FilterAndOrder.DATE_ASC, 1)

        val tasksToDelete = ArrayList<Task>()
        for (i in 0..(tasksInCache.size.div(2))) {
            val taskToInsert = tasksInCache.random()
            if (!tasksToDelete.contains(taskToInsert)) {
                tasksToDelete.add(taskToInsert)
            }
        }

        deleteMultipleTask.deleteTasks(
            tasksToDelete,
            TaskListStateEvent.DeleteMultipleTasksEvent(tasksToDelete)
        ).collect {
            assertEquals(
                DELETE_TASKS_SUCCESS,
                it?.stateMessage?.response?.message
            )
        }
        //confirm tasks were deleted in cache
        val tasksInCacheAfterDelete = taskCacheDataSource.searchTask("", FilterAndOrder.DATE_ASC, 1)

        for (task in tasksToDelete) {
            assertFalse { tasksInCacheAfterDelete.contains(task) }
        }
        //confirm tasks were delete in network
        val tasksInNetworkAfterDelete = taskNetworkDataSource.getAllTasks()

        for (task in tasksToDelete) {
            assertFalse { tasksInNetworkAfterDelete.contains(task) }
        }
        //confirm task were added to 'deleted' node
        val tasksInDeletedAfterDelete = taskNetworkDataSource.getDeletedTasks()
        assertTrue { tasksInDeletedAfterDelete.containsAll(tasksToDelete) }

    }

    @Test
    fun deleteTasks_fail_confirmCacheAndNetworkUpdatedViaSuccessfulOnes() = runBlocking {

        val tasksInCacheBeforeDelete = taskCacheDataSource.searchTask("", FilterAndOrder.DATE_ASC, 1)
        val tasksInNetworkBeforeDelete = taskNetworkDataSource.getAllTasks()

        val validTasks = ArrayList<Task>()
        val invalidTasks = ArrayList<Task>()

        for (i in 0..(tasksInCacheBeforeDelete.size.div(2))) {
            val taskToInsert = tasksInCacheBeforeDelete.random()
            invalidTasks.add(TaskFactory.createRandomTask())
            if (!validTasks.contains(taskToInsert)) {
                validTasks.add(taskToInsert)
            }
        }

        deleteMultipleTask.deleteTasks(
            validTasks + invalidTasks,
            TaskListStateEvent.DeleteMultipleTasksEvent(validTasks + invalidTasks)
        )
            .collect {
                assertEquals(
                    DELETE_TASKS_ERRORS,
                    it?.stateMessage?.response?.message
                )

            }

        //confirm valid tasks deleted
        val deletedTasks = taskNetworkDataSource.getDeletedTasks()
        for (item in validTasks) {
            //confirm deleted in cache
            assertNull(taskCacheDataSource.searchTaskById(item.id))
            //confirm deleted in 'tasks' node in network
            assertNull(taskNetworkDataSource.searchTask(item))
            //confirm task was actually in cache and network before delete operation
            assertTrue { tasksInCacheBeforeDelete.contains(item) }
            assertTrue { tasksInNetworkBeforeDelete.contains(item) }
            //confirm inserted into 'deleted' node in network
            assertTrue {
                deletedTasks.contains(item)
            }
        }
        //confirm invalid tasks did not deleted
        for (item in invalidTasks) {
            assertNull(taskCacheDataSource.searchTaskById(item.id))
            assertNull(taskNetworkDataSource.searchTask(item))
            assertFalse {
                deletedTasks.contains(item)
            }
        }


    }

    @Test
    fun forceException_fail_confirmCacheAndNetworkUnUpdated() = runBlocking {

        val tasksInCacheBeforeDelete = taskCacheDataSource.searchTask("", FilterAndOrder.DATE_ASC, 1)
        val tasksInNetworkBeforeDelete = taskNetworkDataSource.getAllTasks()

        val validTasks = ArrayList<Task>()
        val invalidTasks = ArrayList<Task>()

        for (i in 0..(tasksInCacheBeforeDelete.size.div(2))) {
            val taskToInsert = tasksInCacheBeforeDelete.random()
            invalidTasks.add(
                TaskFactory.createTask(
                    id = FORCE_EXCEPTION,
                    title = UUID.randomUUID().toString()
                )
            )
            if (!validTasks.contains(taskToInsert)) {
                validTasks.add(taskToInsert)
            }
        }

        deleteMultipleTask.deleteTasks(
            validTasks + invalidTasks,
            TaskListStateEvent.DeleteMultipleTasksEvent(validTasks + invalidTasks)
        )
            .collect {
                assertEquals(
                    DELETE_TASKS_ERRORS,
                    it?.stateMessage?.response?.message
                )

            }

        //confirm valid tasks deleted
        val deletedTasks = taskNetworkDataSource.getDeletedTasks()
        for (item in validTasks) {
            //confirm deleted in cache
            assertNull(taskCacheDataSource.searchTaskById(item.id))
            //confirm deleted in 'tasks' node in network
            assertNull(taskNetworkDataSource.searchTask(item))
            //confirm task was actually in cache and network before delete operation
            assertTrue { tasksInCacheBeforeDelete.contains(item) }
            assertTrue { tasksInNetworkBeforeDelete.contains(item) }
            //confirm inserted into 'deleted' node in network
            assertTrue {
                deletedTasks.contains(item)
            }
        }
        //confirm invalid tasks did not deleted
        for (item in invalidTasks) {
            assertNull(taskCacheDataSource.searchTaskById(item.id))
            assertNull(taskNetworkDataSource.searchTask(item))
            assertFalse {
                deletedTasks.contains(item)
            }
        }
    }


}