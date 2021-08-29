package com.example.clean_todo_list.business.interactors.task.tasklist

import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeApiCall
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.presentation.task.tasklist.state.TaskListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class RestoreDeletedTask(
    private val taskCacheDataSource: TaskCacheDataSource,
    private val taskNetworkDataSource: TaskNetworkDataSource
) {

    fun restoreDeletedTask(
        task: Task,
        stateEvent: StateEvent
    ): Flow<DataState<TaskListViewState>?> = flow {

        val cacheResult = safeCacheCall(IO) {
            taskCacheDataSource.insertTask(task)
        }
        val cacheResponse = object : CacheResponseHandler<TaskListViewState, Long>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Long): DataState<TaskListViewState>? {
                return if (resultObj > 0) {
                    DataState.data(
                        response = Response(
                            message = RESTORE_TASK_SUCCESS,
                            uiComponentType = UIComponentType.None,
                            messageType = MessageType.Success
                        ),
                        data = TaskListViewState(
                            taskPendingDelete = TaskListViewState.TaskPendingDelete(
                                task = task
                            )
                        ),
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.error(
                        response = Response(
                            message = RESTORE_TASK_FAILED,
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
            restoreInNetwork(task)
        }
    }

    private suspend fun restoreInNetwork(task: Task) {

        coroutineScope {
            launch {
                //insert into 'tasks' node
                safeApiCall(IO) {
                    taskNetworkDataSource.insertTask(task)
                }
            }
            launch {
                //delete from 'deleted' node
                safeApiCall(IO) {
                    taskNetworkDataSource.deleteDeletedTask(task)
                }
            }
        }


    }

    companion object {

        val RESTORE_TASK_SUCCESS = "Successfully restored the deleted task."
        val RESTORE_TASK_FAILED = "Failed to restore the deleted task."

    }
}