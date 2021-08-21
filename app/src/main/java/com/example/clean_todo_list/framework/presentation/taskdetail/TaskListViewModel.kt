package com.example.clean_todo_list.framework.presentation.taskdetail

import android.content.SharedPreferences
import android.os.Parcelable
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.DataState
import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.business.interactors.tasklist.TaskListInteractors
import com.example.clean_todo_list.framework.datasource.cache.util.FilterAndOrder
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent.*
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListViewState
import com.example.clean_todo_list.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@FlowPreview
class TaskListViewModel
//TODO CHECK FOR @INJECT?
constructor(
    private val taskListInteractors: TaskListInteractors,
    private val sharedPreferences: SharedPreferences,
    private val sharedPrefsEditor: SharedPreferences.Editor
) : BaseViewModel<TaskListViewState>() {
    init {
        setTaskFilterAndOrder(FilterAndOrder.DATE_DESC)
    }

    override fun handleNewData(data: TaskListViewState) {
        printLogD("handleNewData", "data: ${data.toString()}")
        val outdated = getCurrentViewStateOrNew()
        val updatedVieState = TaskListViewState(
            taskList = data.taskList ?: outdated.taskList,
            newTask = data.newTask ?: outdated.newTask,
            taskPendingDelete = data.taskPendingDelete ?: outdated.taskPendingDelete,
            searchQuery = data.searchQuery ?: outdated.searchQuery,
            page = data.page ?: outdated.page,
            isQueryExhausted = data.isQueryExhausted ?: outdated.isQueryExhausted,
            filterAndOrder = data.filterAndOrder ?: outdated.filterAndOrder,
            layoutManagerState = data.layoutManagerState ?: outdated.layoutManagerState,
            numTasksInCache = data.numTasksInCache ?: outdated.numTasksInCache,
        )
        printLogD(
            "handleNewData",
            "taskListsize :${data.taskList?.size} tasklist: ${data.taskList}"
        )
        printLogD("handleNewData", "task ${data.newTask}")
        setViewState(updatedVieState)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<TaskListViewState>?> = when (stateEvent) {

            is InsertNewTaskEvent -> {
                taskListInteractors.insertNewTask.insertTaskTask(
                    title = stateEvent.title,
                    stateEvent = stateEvent
                )
            }

            is DeleteTaskEvent -> {
                taskListInteractors.deleteTask.deleteTask(
                    task = stateEvent.task,
                    stateEvent = stateEvent
                )
            }

            is DeleteMultipleTasksEvent -> {
                taskListInteractors.deleteMultipleTask.deleteTasks(
                    tasks = stateEvent.tasks,
                    stateEvent = stateEvent
                )
            }

            is RestoreDeletedTaskEvent -> {
                taskListInteractors.restoreDeletedTask.restoreDeletedTask(
                    task = stateEvent.task,
                    stateEvent = stateEvent
                )
            }

            is SearchTasksEvent -> {
                if (stateEvent.clearLayoutManagerState) {
                    clearLayoutManagerState()
                }
                taskListInteractors.searchTasks.searchTasks(
                    query = getSearchQuery(),
                    filterAndOrder = getFilterAndOrder(),
                    page = getPage(),
                    stateEvent = stateEvent
                )
            }

            is GetNumTasksInCacheEvent -> {
                taskListInteractors.getNumTasks.getNumOfTasks(
                    stateEvent = stateEvent
                )
            }

            is ChangeTaskDoneStateEvent -> {
                taskListInteractors.changeTaskDoneState.changeTaskDoneState(
                    stateEvent.taskId,
                    stateEvent.isDone,
                    stateEvent
                )
            }

            is CreateStateMessageEvent -> {
                emitStateMessageEvent(
                    stateMessage = stateEvent.stateMessage,
                    stateEvent = stateEvent
                )
            }

            else -> {
                emitInvalidStateEvent(stateEvent)
            }
        }
        launchJob(stateEvent, job)
    }

    private fun getPage(): Int = getCurrentViewStateOrNew().page ?: 1

    private fun getFilterAndOrder(): FilterAndOrder =
        getCurrentViewStateOrNew().filterAndOrder ?: FilterAndOrder.DATE_DESC

    private fun getSearchQuery(): String = getCurrentViewStateOrNew().searchQuery ?: ""

    private fun clearLayoutManagerState() {
        val update = getCurrentViewStateOrNew()
        update.layoutManagerState = null
        setViewState(update)
    }

    private fun setTaskFilterAndOrder(filterAndOrder: FilterAndOrder) {
        val current = getCurrentViewStateOrNew()
        val update = current.copy(filterAndOrder = filterAndOrder)
        setViewState(update)
    }

    override fun initNewViewState(): TaskListViewState = TaskListViewState()

    fun nextPage() {
        if (!isQueryExhausted()) {
            clearLayoutManagerState()
            incrementPageNumber()
            setStateEvent(SearchTasksEvent())
        }
    }

    private fun incrementPageNumber() {
        val outDate = getCurrentViewStateOrNew()
        val update = outDate.copy(
            page = outDate.page?.plus(1) ?: 2
        )
        setViewState(update)
    }

    fun isPaginationExhausted(): Boolean = getTaskListSize() >= getNumTasksInCache()

    private fun getTaskListSize(): Int = getCurrentViewStateOrNew().taskList?.size ?: 0

    private fun getNumTasksInCache(): Int = getCurrentViewStateOrNew().numTasksInCache ?: 0

    fun isQueryExhausted(): Boolean = getCurrentViewStateOrNew().isQueryExhausted ?: true

    fun setQueryExhausted(isExhausted: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.isQueryExhausted = isExhausted
        setViewState(update)
    }

    fun clearList() {
        val update = getCurrentViewStateOrNew()
        update.taskList = ArrayList()
        setViewState(update)
    }

    fun loadFirstPage() {
        setQueryExhausted(false)
        resetPage()
        setStateEvent(SearchTasksEvent())
    }

    private fun resetPage() {
        val update = getCurrentViewStateOrNew()
        update.page = 1
        setViewState(update)
    }

    fun retrieveNumTasksInCache() {
        setStateEvent(GetNumTasksInCacheEvent())
    }

    fun refreshSearchQuery() {
        setQueryExhausted(false)
        setStateEvent(SearchTasksEvent(false))
    }

    fun setLayoutManagerState(layoutManagerState: Parcelable) {
        val update = getCurrentViewStateOrNew()
        update.layoutManagerState = layoutManagerState
        setViewState(update)
    }
}