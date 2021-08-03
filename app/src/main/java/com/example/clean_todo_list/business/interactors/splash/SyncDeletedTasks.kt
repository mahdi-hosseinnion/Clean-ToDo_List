package com.example.clean_todo_list.business.interactors.splash

import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.ApiResponseHandler
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeApiCall
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.DataState
import com.example.clean_todo_list.business.domain.state.StateEvent
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

    suspend fun syncDeletedTasks(stateEvent: StateEvent) {

        val allTasksInNetwork = getAllTasksFromNetwork()

        if (allTasksInNetwork.isNotEmpty()) {
            val cacheResult = safeCacheCall(IO) {
                taskCacheDataSource.deleteTasks(allTasksInNetwork)
            }
            //just for debugging purposes
            object : CacheResponseHandler<Int, Int>(
                response = cacheResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: Int): DataState<Int>? {
                    printLogD("syncNote", "Successfully deleted $resultObj task")
                    return DataState.data(
                        response = null,
                        stateEvent = null,
                        data = resultObj
                    )
                }
            }.getResult()
        }
    }

    private suspend fun getAllTasksFromNetwork(): List<Task> {

        val networkResult = safeApiCall(IO) {
            taskNetworkDataSource.getDeletedTasks()
        }
        val networkResponse = object : ApiResponseHandler<List<Task>, List<Task>>(
            response = networkResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: List<Task>): DataState<List<Task>>? {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }
        }.getResult()
        return networkResponse?.data ?: emptyList()
    }
}