package com.example.clean_todo_list.business.interactors.splash

import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.ApiResponseHandler
import com.example.clean_todo_list.business.data.network.NetworkErrors
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeApiCall
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.util.printLogD
import kotlinx.coroutines.Dispatchers.IO

/**
 * get all tasks in network 'deleted' node
 * if they exist in cache, remove them from cache
 */
class SyncDeletedTasks(
    private val taskCacheDataSource: TaskCacheDataSource,
    private val taskNetworkDataSource: TaskNetworkDataSource
) {

    suspend fun syncDeletedTasks(): DataState<Int>? {
        val networkResponse = getAllTasksFromNetwork()
        val networkStateMessageResponse = networkResponse?.stateMessage?.response

        if (networkStateMessageResponse?.messageType != MessageType.Success) {
            return DataState.error(
                response = Response(
                    message = GET_ALL_TASK_FROM_NETWORK_ERROR,
                    uiComponentType = UIComponentType.None,
                    messageType = MessageType.Error
                ),
                null
            )
        }

        val allTasksInNetwork = networkResponse.data ?: emptyList()

        return if (allTasksInNetwork.isNotEmpty()) {
            val cacheResult = safeCacheCall(IO) {
                taskCacheDataSource.deleteTasks(allTasksInNetwork)
            }
            //just for debugging purposes
            object : CacheResponseHandler<Int, Int>(
                response = cacheResult,
                stateEvent = null
            ) {
                override suspend fun handleSuccess(resultObj: Int): DataState<Int>? {
                    printLogD("SyncDeletedTasks", "Successfully deleted $resultObj task")
                    return DataState.data(
                        response = Response(
                            message = DELETE_ALL_DELETED_TASKS_SUCCESS,
                            uiComponentType = UIComponentType.None,
                            messageType = MessageType.Success
                        ),
                        stateEvent = null,
                        data = resultObj
                    )
                }
            }.getResult()
        } else {
            return DataState.data(
                response = Response(
                    message = THERE_IS_NO_TASK_IN_DELETE_NODE_TO_DELETE,
                    uiComponentType = UIComponentType.None,
                    messageType = MessageType.Success
                ),
                null,
                stateEvent = null
            )
        }
    }

    private suspend fun getAllTasksFromNetwork(): DataState<List<Task>>? {

        val networkResult = safeApiCall(IO) {
            taskNetworkDataSource.getDeletedTasks()
        }
        return object : ApiResponseHandler<List<Task>, List<Task>>(
            response = networkResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: List<Task>): DataState<List<Task>>? {
                return DataState.data(
                    response = Response(
                        message = GET_ALL_TASK_FROM_NETWORK_SUCCESS,
                        uiComponentType = UIComponentType.None,
                        messageType = MessageType.Success
                    ),
                    data = resultObj,
                    stateEvent = null
                )
            }
        }.getResult()
    }

    companion object {
        private const val GET_ALL_TASK_FROM_NETWORK_SUCCESS =
            "Successfully get all tasks from network"
        const val GET_ALL_TASK_FROM_NETWORK_ERROR =
            "Unable get all tasks from network"
         const val THERE_IS_NO_TASK_IN_DELETE_NODE_TO_DELETE =
            "There is not any task in delete task to delete"
        private const val DELETE_ALL_DELETED_TASKS_SUCCESS =
            "Successfully delete all deleted tasks"

    }
}