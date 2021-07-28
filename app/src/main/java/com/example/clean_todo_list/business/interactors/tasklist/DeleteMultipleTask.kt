package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeApiCall
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.business.interactors.common.DeleteTask
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteMultipleTask(
    private val taskCacheDataSource: TaskCacheDataSource,
    private val taskNetworkDataSource: TaskNetworkDataSource
) {

    /**
     * Logic:
     * 1. execute all the deletes and save result into an ArrayList<DataState<TaskListViewState>>
     * 2a. If one of the results is a failure, emit an "error" response
     * 2b. If all success, emit success response
     * 3. Update network with tasks that were successfully deleted
     */
    fun deleteMultipleTasks(
        tasks: List<Task>,
        stateEvent: StateEvent
    ): Flow<DataState<TaskListViewState>?> = flow {
        // set false if an error occurs when deleting any of the tasks from cache
        var allTaskDeletedSuccessfully = true
        val successfullyDeletedTasks = ArrayList<Task>()

        for (task in tasks) {
            val didTaskDeleted = deleteTaskAndReturnResult(task.id)
            if (didTaskDeleted) {
                successfullyDeletedTasks.add(task)
            } else {
                allTaskDeletedSuccessfully = false
            }
        }

        val finalResult = if (allTaskDeletedSuccessfully) {
            DataState.data<TaskListViewState>(
                response = Response(
                    message = DELETE_TASKS_SUCCESS,
                    uiComponentType = UIComponentType.Toast,
                    messageType = MessageType.Success
                ),
                data = null,
                stateEvent = stateEvent
            )
        } else {
            DataState.error(
                response = Response(
                    message = DELETE_TASKS_ERRORS,
                    uiComponentType = UIComponentType.Dialog,
                    messageType = MessageType.Error
                ),
                stateEvent = stateEvent
            )
        }
        emit(finalResult)
        deleteMultipleTasksInNetwork(successfullyDeletedTasks)

    }

    private suspend fun deleteMultipleTasksInNetwork(successfullyDeletedTasks: List<Task>) {
        for (task in successfullyDeletedTasks) {
            safeApiCall(IO) {
                // delete from "tasks" node
                taskNetworkDataSource.deleteTask(task.id)
            }
            // insert into "deletes" node
            safeApiCall(IO) {
                taskNetworkDataSource.insertDeletedTask(task)
            }
        }
    }

    private suspend fun deleteTaskAndReturnResult(primaryKey: String): Boolean {
        val cacheResult = safeCacheCall(IO) {
            taskCacheDataSource.deleteTask(primaryKey)
        }

        val cacheResponse = object : CacheResponseHandler<TaskListViewState, Int>(
            response = cacheResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: Int): DataState<TaskListViewState>? {
                val response = if (resultObj > 0) {
                    //success
                    Response(
                        message = DeleteTask.DELETE_TASK_SUCCESS,
                        uiComponentType = UIComponentType.None,
                        messageType = MessageType.Success
                    )
                } else {
                    Response(
                        message = DeleteTask.DELETE_TASK_FAILED,
                        uiComponentType = UIComponentType.Toast,
                        messageType = MessageType.Error
                    )
                }
                return DataState.data(
                    response = response,
                    data = null,
                    stateEvent = null
                )
            }
        }.getResult()

        return cacheResponse?.stateMessage?.response?.messageType == MessageType.Success
    }

    companion object {
        val DELETE_TASKS_SUCCESS = "Successfully deleted tasks."
        val DELETE_TASKS_ERRORS =
            "Not all the tasks you selected were deleted. There was some errors."
        val DELETE_TASKS_YOU_MUST_SELECT = "You haven't selected any tasks to delete."
        val DELETE_TASKS_ARE_YOU_SURE = "Are you sure you want to delete these?"
    }

}