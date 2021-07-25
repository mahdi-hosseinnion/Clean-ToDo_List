package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListViewState
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
    ): Flow<DataState<TaskListViewState>> = flow {

        val newTask = TaskFactory.createTask(
            id = id,
            title = title,
            body = null,
            isDone = false
        )

        val cacheResult = taskCacheDataSource.insertTask(newTask)

        val cacheResponse: DataState<TaskListViewState> = if (cacheResult > 0) {
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
        emit(cacheResponse)

        updateNetwork(cacheResponse.stateMessage?.response?.messageType, newTask)

    }

    private suspend fun updateNetwork(messageType: MessageType?, newTask: Task) {
        if (messageType == MessageType.Success) {
            taskNetworkDataSource.insertOrUpdateTask(newTask)
        }
    }

    companion object {
        const val INSERT_TASK_SUCCESS = "Successfully inserted new task."
        const val INSERT_TASK_FAILED = "Failed to insert new task."
    }
}