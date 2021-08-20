package com.example.clean_todo_list.framework.presentation.taskdetail

import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.business.interactors.taskdetail.TaskDetailInteractors
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import com.example.clean_todo_list.framework.presentation.taskdetail.state.TaskDetailViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class TaskDetailViewModel
//TODO CHECK FOR @INJECT
constructor(
    private val taskDetailInteractors: TaskDetailInteractors
) : BaseViewModel<TaskDetailViewState>() {

    override fun handleNewData(data: TaskDetailViewState) {
//        TODO("Not yet implemented")
    }

    override fun setStateEvent(stateEvent: StateEvent) {
//        TODO("Not yet implemented")
    }

    override fun initNewViewState(): TaskDetailViewState = TaskDetailViewState()

}