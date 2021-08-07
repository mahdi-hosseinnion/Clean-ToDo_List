package com.example.clean_todo_list.business.interactors.taskdetail

import com.example.clean_todo_list.business.interactors.common.ChangeTaskDoneState
import com.example.clean_todo_list.business.interactors.common.DeleteTask
import com.example.clean_todo_list.framework.presentation.taskdetail.state.TaskDetailViewState

class TaskDetailInteractors
constructor(
    val deleteTask: DeleteTask<TaskDetailViewState>,
    val changeTaskDoneState: ChangeTaskDoneState<TaskDetailViewState>,
    val updateTask: UpdateTask
)