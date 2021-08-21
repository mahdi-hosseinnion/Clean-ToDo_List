package com.example.clean_todo_list.framework.presentation.tasklist.state

import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.business.domain.state.StateMessage

sealed class TaskListStateEvent : StateEvent {

    class InsertNewTaskEvent(
        val title: String
    ) : TaskListStateEvent() {

        override fun errorInfo(): String {
            return "Error inserting new task."
        }

        override fun eventName(): String {
            return "InsertNewTaskEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    // for testing
/*    class InsertMultipleTasksEvent(
        val numTasks: Int
    ) : TaskListStateEvent() {

        override fun errorInfo(): String {
            return "Error inserting the tasks."
        }

        override fun eventName(): String {
            return "InsertMultipleTasksEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }*/

    class DeleteTaskEvent(
        val task: Task
    ) : TaskListStateEvent() {

        override fun errorInfo(): String {
            return "Error deleting task."
        }

        override fun eventName(): String {
            return "DeleteTaskEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class ChangeTaskDoneStateEvent(
        val taskId: String,
        val isDone: Boolean
    ) : TaskListStateEvent() {

        override fun errorInfo(): String {
            return "Error updating task done state."
        }

        override fun eventName(): String {
            return "ChangeTaskDoneState"
        }

        override fun shouldDisplayProgressBar() = false
    }

    class DeleteMultipleTasksEvent(
        val tasks: List<Task>
    ) : TaskListStateEvent() {

        override fun errorInfo(): String {
            return "Error deleting the selected tasks."
        }

        override fun eventName(): String {
            return "DeleteMultipleTasksEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class RestoreDeletedTaskEvent(
        val task: Task
    ) : TaskListStateEvent() {

        override fun errorInfo(): String {
            return "Error restoring the task that was deleted."
        }

        override fun eventName(): String {
            return "RestoreDeletedTaskEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }

    class SearchTasksEvent(
        val clearLayoutManagerState: Boolean = true
    ) : TaskListStateEvent() {

        override fun errorInfo(): String {
            return "Error getting list of tasks."
        }

        override fun eventName(): String {
            return "SearchTasksEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class GetNumTasksInCacheEvent : TaskListStateEvent() {

        override fun errorInfo(): String {
            return "Error getting the number of tasks from the cache."
        }

        override fun eventName(): String {
            return "GetNumTasksInCacheEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class CreateStateMessageEvent(
        val stateMessage: StateMessage
    ) : TaskListStateEvent() {

        override fun errorInfo(): String {
            return "Error creating a new state message."
        }

        override fun eventName(): String {
            return "CreateStateMessageEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }
}