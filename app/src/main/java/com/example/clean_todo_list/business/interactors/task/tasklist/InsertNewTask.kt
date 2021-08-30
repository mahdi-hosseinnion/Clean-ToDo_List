package com.example.clean_todo_list.business.interactors.task.tasklist

import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.task.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeApiCall
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.presentation.task.tasklist.state.TaskListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InsertNewTask(
    private val taskCacheDataSource: TaskCacheDataSource,
    private val taskNetworkDataSource: TaskNetworkDataSource
) {
    fun insertTaskTask(
        id: String? = null,
        title: String,
        stateEvent: StateEvent
    ): Flow<DataState<TaskListViewState>?> = flow {

        val newTask = TaskFactory.createTask(
            id = id,
            title = title,
            body = null,
            isDone = false
        )

        val cacheResult = safeCacheCall(IO) {
            taskCacheDataSource.insertTask(newTask)
        }
        val cacheResponse = object : CacheResponseHandler<TaskListViewState, Long>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Long): DataState<TaskListViewState>? {
                return if (resultObj > 0) {
                    //success case b/c if insert was successful sqlite return rawId
                    val viewState = TaskListViewState(newTask = newTask)

                    DataState.data(
                        response = Response(
                            message = INSERT_TASK_SUCCESS,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Success
                        ),
                        data = viewState,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.error(
                        response = Response(
                            message = INSERT_TASK_FAILED,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        ),
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)

        updateNetwork(cacheResponse?.stateMessage?.response?.messageType, newTask)

    }

    private suspend fun updateNetwork(messageType: MessageType?, newTask: Task) {
        if (messageType == MessageType.Success) {
            //we don't care about api result b/c app will sync data in splash screen anyway
            safeApiCall(IO) {
                taskNetworkDataSource.insertTask(newTask)
            }
        }
    }

    companion object {
        const val INSERT_TASK_SUCCESS = "Successfully inserted new task."
        const val INSERT_TASK_FAILED = "Failed to insert new task."
    }
}