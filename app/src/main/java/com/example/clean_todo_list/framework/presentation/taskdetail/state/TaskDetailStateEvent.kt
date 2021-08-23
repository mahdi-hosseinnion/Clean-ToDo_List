package com.example.clean_todo_list.framework.presentation.taskdetail.state

import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.business.domain.state.StateMessage

sealed class TaskDetailStateEvent : StateEvent {

    data class UpdateTaskEvent(
        val task: Task
    ) : TaskDetailStateEvent() {

        override fun errorInfo(): String {
            return "Error updating task."
        }

        override fun eventName(): String {
            return "UpdateTaskEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class DeleteTaskDetailEvent(
        val task: Task
    ) : TaskDetailStateEvent() {

        override fun errorInfo(): String {
            return "Error deleting task."
        }

        override fun eventName(): String {
            return "DeleteTaskEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class CreateDetailStateMessageEvent(
        val stateMessage: StateMessage
    ) : TaskDetailStateEvent() {

        override fun errorInfo(): String {
            return "Error creating a new state message."
        }

        override fun eventName(): String {
            return "CreateStateMessageEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }

}