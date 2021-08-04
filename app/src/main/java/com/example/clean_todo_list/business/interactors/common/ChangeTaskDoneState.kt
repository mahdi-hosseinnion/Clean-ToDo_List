package com.example.clean_todo_list.business.interactors.common

import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeApiCall
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class ChangeTaskDoneState<_ViewState : ViewState>(
    private val taskCacheDataSource: TaskCacheDataSource,
    private val taskNetworkDataSource: TaskNetworkDataSource
) {

    fun changeTaskDoneState(
        taskId: String,
        isDone: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<_ViewState>?> = flow {

        val cacheResult = safeCacheCall(IO) {
            taskCacheDataSource.updateIsDone(
                primaryKey = taskId,
                isDone = isDone
            )
        }
        val cacheResponse = object : CacheResponseHandler<_ViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Int): DataState<_ViewState>? {
                return if (resultObj > 0) {
                    //success
                    DataState.data(
                        response = Response(
                            message = UPDATE_TASK_DONE_STATE_SUCCESS,
                            uiComponentType = UIComponentType.None,
                            messageType = MessageType.Success
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.error(
                        response = Response(
                            message = UPDATE_TASK_DONE_STATE_FAILED,
                            uiComponentType = UIComponentType.None,
                            messageType = MessageType.Success
                        ),
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)

        if (cacheResponse?.stateMessage?.response?.messageType == MessageType.Success) {
            changeTaskDoneStateInNetwork(taskId, isDone)
        }
    }

    private suspend fun changeTaskDoneStateInNetwork(
        taskId: String,
        isDone: Boolean
    ) {
        safeApiCall(IO) {
            taskNetworkDataSource.updateIsDone(taskId, isDone)
        }
    }

    companion object {
        val UPDATE_TASK_DONE_STATE_SUCCESS = "Successfully update done state."
        val UPDATE_TASK_DONE_STATE_FAILED = "Failed to update done state."
    }
}