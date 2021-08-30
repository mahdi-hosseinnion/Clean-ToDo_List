package com.example.clean_todo_list.business.interactors.common

import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.task.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeApiCall
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteTask<_ViewState>(
    private val taskCacheDataSource: TaskCacheDataSource,
    private val taskNetworkDataSource: TaskNetworkDataSource
) {

    fun deleteTask(
        task: Task,
        stateEvent: StateEvent
    ): Flow<DataState<_ViewState>?> = flow {

        val cacheResult = safeCacheCall(IO) {
            taskCacheDataSource.deleteTask(task.id)
        }

        val cacheResponse = object : CacheResponseHandler<_ViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Int): DataState<_ViewState>? {
                val response = if (resultObj > 0) {
                    //success
                    Response(
                        message = DELETE_TASK_SUCCESS,
                        uiComponentType = UIComponentType.None,
                        messageType = MessageType.Success
                    )
                } else {
                    Response(
                        message = DELETE_TASK_FAILED,
                        uiComponentType = UIComponentType.Toast,
                        messageType = MessageType.Error
                    )
                }
                return DataState.data(
                    response = response,
                    data = null,
                    stateEvent = stateEvent
                )
            }
        }.getResult()

        emit(cacheResponse)

        if (cacheResponse?.stateMessage?.response?.messageType == MessageType.Success) {
            deleteInNetwork(task)
        }
    }

    private suspend fun deleteInNetwork(task: Task) {
        // delete from 'tasks' node
        safeApiCall(IO) {
            taskNetworkDataSource.deleteTask(task.id)
        }
        // insert into 'deletes' node
        safeApiCall(IO) {
            taskNetworkDataSource.insertDeletedTask(task)
        }
    }

    companion object {
        val DELETE_TASK_SUCCESS = "Successfully deleted task."
        val DELETE_TASK_PENDING = "Delete pending..."
        val DELETE_TASK_FAILED = "Failed to delete task."
        val DELETE_ARE_YOU_SURE = "Are you sure you want to delete this?"
    }
}