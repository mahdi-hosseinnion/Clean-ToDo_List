package com.example.clean_todo_list.framework.presentation.tasklist

import android.content.SharedPreferences
import android.os.Parcelable
import androidx.lifecycle.asLiveData
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.business.interactors.tasklist.TaskListInteractors
import com.example.clean_todo_list.framework.datasource.cache.util.APP_DEFAULT_SORT
import com.example.clean_todo_list.framework.datasource.cache.util.SORT_AND_ORDER_SP
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder.*
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent.*
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListViewState
import com.example.clean_todo_list.util.cLog
import com.example.clean_todo_list.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
//TODO("make sure inject is necessary)
@Singleton
class TaskListViewModel
@Inject
constructor(
    private val taskListInteractors: TaskListInteractors,
    private val sharedPreferences: SharedPreferences,
    private val sharedPrefsEditor: SharedPreferences.Editor
) : BaseViewModel<TaskListViewState>() {

    init {
        setSort(getSavedSort())
    }

    override fun handleNewData(data: TaskListViewState) {
        val outdated = getCurrentViewStateOrNew()

        val updatedVieState = TaskListViewState(
            newTask = data.newTask ?: outdated.newTask,
            taskPendingDelete = data.taskPendingDelete ?: outdated.taskPendingDelete,
            searchQuery = data.searchQuery ?: outdated.searchQuery,
            page = data.page ?: outdated.page,
            isQueryExhausted = data.isQueryExhausted ?: outdated.isQueryExhausted,
            sortAndOrder = data.sortAndOrder ?: outdated.sortAndOrder,
            layoutManagerState = data.layoutManagerState ?: outdated.layoutManagerState,
            numTasksInCache = data.numTasksInCache ?: outdated.numTasksInCache,
        )
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

    override fun initNewViewState(): TaskListViewState = TaskListViewState()

    fun nextPage() {
        //TODO NOT IMPLEMENTED
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
            sort
        } else {
            setStateEvent(
                CreateStateMessageEvent(
                    StateMessage(
                        Response(
                            message = NO_SORT_FOUND,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        )
                    )
                )
            )
            cLog("THERE IS NO SORT IN VIEW STATE ", "$TAG getSort")
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
        with(sharedPrefsEditor) {
            putString(SORT_AND_ORDER_SP, newSort.name)
            apply()
        }
    }

    private fun getSavedSort(): SortAndOrder {
        val default = APP_DEFAULT_SORT.name
        return when (sharedPreferences.getString(
            SORT_AND_ORDER_SP,
            default
        )) {
            CREATED_DATE_DESC.name -> CREATED_DATE_DESC

            CREATED_DATE_ASC.name -> CREATED_DATE_ASC

            NAME_DESC.name -> NAME_DESC

            NAME_ACS.name -> NAME_ACS

            else -> APP_DEFAULT_SORT
        }
    }


    companion object {
        private const val TAG = "TaskListViewModel"
        private const val NO_SORT_FOUND = "No sort found!"
    }
}