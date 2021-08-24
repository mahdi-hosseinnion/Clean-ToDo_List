package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.interactors.common.ChangeTaskDoneState
import com.example.clean_todo_list.business.interactors.common.DeleteTask
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

//all use cases for this fragment
@FlowPreview
@ExperimentalCoroutinesApi
class TaskListInteractors(
    val insertNewTask: InsertNewTask,
    val deleteTask: DeleteTask<TaskListViewState>,
    val searchTasks: SearchTasks,
    val observeTaskInCache: ObserveTaskInCache,
    val getNumTasks: GetNumTasks,
    val restoreDeletedTask: RestoreDeletedTask,
    val deleteMultipleTask: DeleteMultipleTask,
    val changeTaskDoneState: ChangeTaskDoneState<TaskListViewState>
)
