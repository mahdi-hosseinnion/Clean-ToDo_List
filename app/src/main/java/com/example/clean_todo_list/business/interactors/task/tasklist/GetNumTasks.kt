package com.example.clean_todo_list.business.interactors.task.tasklist

import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.presentation.task.tasklist.state.TaskListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetNumTasks(
    private val taskCacheDataSource: TaskCacheDataSource
) {

    fun getNumOfTasks(
        stateEvent: StateEvent
    ): Flow<DataState<TaskListViewState>?> = flow {

        val cacheResult = safeCacheCall(IO) {
            taskCacheDataSource.getNumOfTasks()
        }

        val cacheResponse = object : CacheResponseHandler<TaskListViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Int): DataState<TaskListViewState>? {
                val viewState = TaskListViewState(numTasksInCache = resultObj)
                return DataState.data(
                    response = Response(
                        message = GET_NUM_TASKS_SUCCESS,
                        uiComponentType = UIComponentType.None,
                        messageType = MessageType.Success
                    ),
                    data = viewState,
                    stateEvent = stateEvent
                )
            }
        }.getResult()

        emit(cacheResponse)
    }

    companion object {
        val GET_NUM_TASKS_SUCCESS = "Successfully retrieved the number of tasks from the cache."
        val GET_NUM_TASKS_FAILED = "Failed to get the number of tasks from the cache."
    }
}