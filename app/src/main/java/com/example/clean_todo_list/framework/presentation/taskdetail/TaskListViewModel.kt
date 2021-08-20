package com.example.clean_todo_list.framework.presentation.taskdetail

import android.content.SharedPreferences
import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.business.interactors.tasklist.TaskListInteractors
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class TaskListViewModel
//TODO CHECK FOR @INJECT?
constructor(
    private val taskListInteractors: TaskListInteractors,
    private val sharedPreferences: SharedPreferences,
    private val sharedPrefsEditor: SharedPreferences.Editor
) : BaseViewModel<TaskListViewState>() {

    override fun handleNewData(data: TaskListViewState) {
//        TODO("Not yet implemented")
    }

    override fun setStateEvent(stateEvent: StateEvent) {
//        TODO("Not yet implemented")
    }

    override fun initNewViewState(): TaskListViewState = TaskListViewState()
}