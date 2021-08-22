package com.example.clean_todo_list.framework.presentation.taskdetail

import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.DataState
import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.business.interactors.taskdetail.TaskDetailInteractors
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import com.example.clean_todo_list.framework.presentation.taskdetail.state.TaskDetailViewState
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListViewState
import com.example.clean_todo_list.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@FlowPreview
class TaskDetailViewModel
//TODO CHECK FOR @INJECT
constructor(
    private val taskDetailInteractors: TaskDetailInteractors
) : BaseViewModel<TaskDetailViewState>() {

    override fun handleNewData(data: TaskDetailViewState) {
        val outdated = getCurrentViewStateOrNew()
        val updatedVieState = TaskDetailViewState(
            task = data.task ?: outdated.task,
            isUpdatePending = data.isUpdatePending ?: outdated.isUpdatePending
        )
        setViewState(updatedVieState)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<TaskDetailViewState>?> = when (stateEvent) {
            is TaskListStateEvent.CreateStateMessageEvent -> {
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

    override fun initNewViewState(): TaskDetailViewState = TaskDetailViewState()

    fun setTask(selectedTask: Task) {
        handleNewData(
            TaskDetailViewState(task = selectedTask)
        )
    }

}