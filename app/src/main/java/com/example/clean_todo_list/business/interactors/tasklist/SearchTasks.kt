package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.data.cache.CacheResponseHandler
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.util.safeCacheCall
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutionKt

class SearchTasks(
    private val taskCacheDataSource: TaskCacheDataSource
) {

    fun searchTasks(
        query: String,
        filterAndOrder: String,
        page: Int,
        stateEvent: StateEvent
    ): Flow<DataState<TaskListViewState>?> = flow {
        var updatedPage = page
        if (page <= 0) {
            updatedPage = 1
        }
        val cacheResult = safeCacheCall(IO) {
            taskCacheDataSource.searchTask(
                query, filterAndOrder, updatedPage
            )
        }

        val cacheResponse = object : CacheResponseHandler<TaskListViewState, List<Task>>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: List<Task>): DataState<TaskListViewState>? {
                var message = SEARCH_TASKS_SUCCESS
                var uiComponentType: UIComponentType = UIComponentType.None
                if (resultObj.isEmpty()) {
                    message = SEARCH_TASKS_NO_MATCHING_RESULTS
                    uiComponentType = UIComponentType.Toast
                }
                return DataState.data(
                    response = Response(
                        message = message,
                        uiComponentType = uiComponentType,
                        messageType = MessageType.Success
                    ),
                    data = TaskListViewState(taskList = ArrayList(resultObj)),
                    stateEvent = stateEvent
                )
            }
        }.getResult()

        emit(cacheResponse)

    }

    companion object {
        val SEARCH_TASKS_SUCCESS = "Successfully retrieved list of tasks."
        val SEARCH_TASKS_NO_MATCHING_RESULTS = "There are no tasks that match that query."
        val SEARCH_TASKS_FAILED = "Failed to retrieve the list of tasks."

    }
}