package com.example.clean_todo_list.framework.presentation.taskdetail

import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.business.interactors.taskdetail.TaskDetailInteractors
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import com.example.clean_todo_list.framework.presentation.taskdetail.state.TaskDetailStateEvent
import com.example.clean_todo_list.framework.presentation.taskdetail.state.TaskDetailViewState
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent
import com.example.clean_todo_list.util.cLog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

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
            originalTask = data.originalTask ?: outdated.originalTask,
            isUpdatePending = data.isUpdatePending ?: outdated.isUpdatePending
        )
        setViewState(updatedVieState)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<TaskDetailViewState>?> = when (stateEvent) {
            is TaskListStateEvent.DeleteTaskEvent -> {
                taskDetailInteractors.deleteTask.deleteTask(
                    stateEvent.task,
                    stateEvent
                )
            }
            is TaskDetailStateEvent.UpdateTaskEvent -> {
                taskDetailInteractors.updateTask.updateTask(
                    stateEvent.task,
                    stateEvent
                )
            }
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

    fun setOriginalTask(selectedTask: Task) {
        handleNewData(
            TaskDetailViewState(
                task = selectedTask,
                originalTask = selectedTask
            )
        )
        titleListener.value = selectedTask.title
        bodyListener.value = selectedTask.body
    }

    fun deleteTask() {
        val task = getCurrentViewStateOrNew().task

        val stateEvent = if (task != null) {
            TaskListStateEvent.DeleteTaskEvent(task = task)

        } else {
            cLog("task in viewState is null, unable to delete", "$TAG , deleteTask")
            TaskListStateEvent.CreateStateMessageEvent(
                stateMessage = StateMessage(
                    response = Response(
                        message = UNABLE_TO_DELETE_TASK,
                        uiComponentType = UIComponentType.Toast,
                        messageType = MessageType.Error
                    )
                )
            )
        }

        setStateEvent(stateEvent)

    }

    fun updateTask() {
        val task = getCurrentViewStateOrNew().task

        val stateEvent = if (task != null) {
            TaskDetailStateEvent.UpdateTaskEvent(task = task)

        } else {
            cLog("task in viewState is null, unable to update", "$TAG , updateTask")
            TaskListStateEvent.CreateStateMessageEvent(
                stateMessage = StateMessage(
                    response = Response(
                        message = UNABLE_TO_UPDATE_TASK,
                        uiComponentType = UIComponentType.Toast,
                        messageType = MessageType.Error
                    )
                )
            )
        }

        setStateEvent(stateEvent)
    }

    fun setTitle(title: String) {
        val task = getCurrentViewStateOrNew().task
        task?.let {
            handleNewData(
                TaskDetailViewState(task = task.copy(title = title))
            )
            titleListener.value = title

        }
    }

    fun setBody(body: String) {
        val task = getCurrentViewStateOrNew().task
        task?.let {
            handleNewData(
                TaskDetailViewState(task = task.copy(body = body))
            )
            bodyListener.value = body
        }
    }

    private val titleListener = MutableStateFlow(getCurrentViewStateOrNew().originalTask?.title)

    private val bodyListener = MutableStateFlow(getCurrentViewStateOrNew().originalTask?.body)

    val shouldDisplaySaveEditButton: Flow<Boolean> =
        combine(titleListener, bodyListener) { title, body ->
            val originalTitle = getCurrentViewStateOrNew().originalTask?.title ?: ""
            val originalBody = getCurrentViewStateOrNew().originalTask?.body ?: ""

            return@combine title != originalTitle || body != originalBody
        }

    companion object {
        private const val TAG = "TaskDetailViewModel"
        private const val UNABLE_TO_DELETE_TASK = "Unable to delete task \n Error:100"
        private const val UNABLE_TO_UPDATE_TASK = "Unable to update task \n Error:101"
    }
}