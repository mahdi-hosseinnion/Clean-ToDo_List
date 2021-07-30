package com.example.clean_todo_list.business.interactors.splash

import android.util.Log
import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.ApiResponseHandler
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeApiCall
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.DataState
import com.example.clean_todo_list.business.domain.state.Response
import com.example.clean_todo_list.business.domain.state.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext

class SyncTasks
    (
    private val taskCacheDataSource: TaskCacheDataSource,
    private val taskNetworkDataSource: TaskNetworkDataSource
) {

    private val TAG = "SyncTasks"

    suspend fun syncTasks() {
        val allTasksInCache = getAllTasksInCache()

        val allTasksInNetwork = getAllTasksInNetwork()

        syncNetworkTasksWithCachedTasks(
            tasksInCache = ArrayList(allTasksInCache),
            tasksInNetwork = allTasksInNetwork
        )
    }


    private suspend fun getAllTasksInCache(): List<Task> {

        val cacheResult = safeCacheCall(IO) {
            taskCacheDataSource.getAllTasks()
        }

        val cacheResponse = object : CacheResponseHandler<List<Task>, List<Task>>(
            response = cacheResult,
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

        return cacheResponse?.data ?: ArrayList()
    }

    private suspend fun getAllTasksInNetwork(): List<Task> {

        val networkResult = safeApiCall(IO) {
            taskNetworkDataSource.getAllTasks()
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

        return networkResponse?.data ?: ArrayList()
    }

    /**
     * convert task list to hashmap(to be able to get it with id)
     * loop throw all task in network if there are not in cache insert them
     * if there are check for update
     * while looping, remove task that there are in both network and cache
     * if there was any left (after delete) it means that there are in cache but
     * not in network so insert them into network
     */
    private suspend fun syncNetworkTasksWithCachedTasks(
        tasksInCache: ArrayList<Task>,
        tasksInNetwork: List<Task>
    ) = withContext(IO) {

        try {
            val cacheHashMap = HashMap<String, Task>()

            for (taskInCache in tasksInCache) {
                cacheHashMap[taskInCache.id] = taskInCache
            }
            for (taskInNetwork in tasksInNetwork) {
                cacheHashMap[taskInNetwork.id]?.let { cachedTask ->
                    tasksInCache.remove(cachedTask)
                    checkForUpdate(cachedTask = cachedTask, networkTask = taskInNetwork)
                } ?: taskCacheDataSource.insertTask(taskInNetwork)
            }
            //remaining cache(does not exist in network)
            for (task in tasksInCache) {
                taskNetworkDataSource.insertOrUpdateTask(task)
            }

        } catch (e: Exception) {
            Log.e(TAG, "syncNetworkTasksWithCachedTasks: ${e.message}", e)
        }

    }

    private suspend fun checkForUpdate(
        cachedTask: Task,
        networkTask: Task
    ) {
        if (cachedTask.updated_at > networkTask.updated_at) {
            // update network (cache has newer data)
            safeApiCall(IO) {
                taskNetworkDataSource.insertOrUpdateTask(cachedTask)
            }
        } else {
            // update cache (network has newer data)
            safeCacheCall(IO) {
                taskCacheDataSource.updateTask(
                    primaryKey = networkTask.id,
                    newTitle = networkTask.title,
                    newBody = networkTask.body,
                    newIsDone = networkTask.isDone
                )
            }
        }
    }
}