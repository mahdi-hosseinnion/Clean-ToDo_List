package com.example.clean_todo_list.framework.presentation.taskdetail

import android.content.SharedPreferences
import android.os.Parcelable
import androidx.lifecycle.asLiveData
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.business.interactors.tasklist.TaskListInteractors
import com.example.clean_todo_list.framework.datasource.cache.util.APP_DEFAULT_SORT
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent.*
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListViewState
import com.example.clean_todo_list.util.cLog
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
        setSort(APP_DEFAULT_SORT)
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
            sortAndOrder = data.sortAndOrder ?: outdated.sortAndOrder,
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
                    sortAndOrder = getSort(),
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

    private val _items = taskListInteractors.observeTaskInCache.execute(
        defaultQuery = getSearchQuery(),
        defaultSortAndOrder = getSort(),
        defaultPage = getPage()
    ).asLiveData()

    val items = _items

    private fun getPage(): Int = getCurrentViewStateOrNew().page ?: 1

    private fun getSearchQuery(): String = getCurrentViewStateOrNew().searchQuery ?: ""

    private fun clearLayoutManagerState() {
        val update = getCurrentViewStateOrNew()
        update.layoutManagerState = null
        setViewState(update)
    }

    override fun initNewViewState(): TaskListViewState = TaskListViewState()

    fun nextPage() {
        if (!isQueryExhausted()) {
            clearLayoutManagerState()
            incrementPageNumber()
        }
    }

    private fun incrementPageNumber() {
        val outDate = getCurrentViewStateOrNew()
        val newPage = outDate.page?.plus(1) ?: 2
        taskListInteractors.observeTaskInCache.setPage(newPage)
        val update = outDate.copy(
            page = newPage
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

    fun setQuery(query: String) {
        taskListInteractors.observeTaskInCache.setQuery(query)
    }

    fun getSort(): SortAndOrder {
        val sort = getCurrentViewStateOrNew().sortAndOrder
        return if (sort != null) {
            setStateEvent(
                CreateStateMessageEvent(
                    StateMessage(
                        Response(
                            message = "No sort found!",
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        )
                    )
                )
            )
            cLog("THERE IS NO SORT IN VIEW STATE ", "$TAG getSort")
            sort
        } else {
            APP_DEFAULT_SORT
        }
    }

    fun setSort(newSort: SortAndOrder) {
        val current = getCurrentViewStateOrNew()
        val update = current.copy(sortAndOrder = newSort)
        setViewState(update)
        taskListInteractors.observeTaskInCache.setSortAndOrder(newSort)
    }

    fun saveNewSort(newSort: SortAndOrder) {
//        TODO("Not yet implemented")
    }


    companion object {
        private const val TAG = "TaskListViewModel"
        private const val NO_SORT_FOUND = "No sort found!"
    }
}