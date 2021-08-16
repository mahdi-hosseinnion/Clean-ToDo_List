package com.example.clean_todo_list.business.interactors.taskdetail

import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeApiCall
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.business.domain.util.DateUtil
import com.example.clean_todo_list.framework.presentation.taskdetail.state.TaskDetailViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateTask(
    private val taskCacheDataSource: TaskCacheDataSource,
    private val taskNetworkDataSource: TaskNetworkDataSource
) {

    fun updateTask(
        task: Task,
        stateEvent: StateEvent
    ): Flow<DataState<TaskDetailViewState>?> = flow {
        //updated at in network and cache should be the same (prevent use less sync)

        val updated_at = DateUtil.getCurrentTimestamp()
        val cacheResult = safeCacheCall(IO) {
            taskCacheDataSource.updateTask(
                task.id,
                task.title,
                task.body,
                task.isDone,
                updated_at
            )
        }

        val cacheResponse = object : CacheResponseHandler<TaskDetailViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Int): DataState<TaskDetailViewState>? {
                return if (resultObj > 0) {
                    DataState.data(
                        response = Response(
                            message = UPDATE_TASK_SUCCESS,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Success
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.error(
                        response = Response(
                            message = UPDATE_TASK_FAILED,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        ),
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)

        if (cacheResponse?.stateMessage?.response?.messageType == MessageType.Success) {
            updateTaskInNetwork(task, updated_at)
        }
    }

    private suspend fun updateTaskInNetwork(task: Task, updated_at: Long) {
        safeApiCall(IO) {
            taskNetworkDataSource.updateTask(task, updated_at)
        }
    }

    companion object {
        val UPDATE_TASK_SUCCESS = "Successfully updated task."
        val UPDATE_TASK_FAILED = "Failed to update task."
        val UPDATE_TASK_FAILED_PK = "Update failed. task is missing primary key."

    }
}