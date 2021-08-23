package com.example.clean_todo_list.framework.presentation.taskdetail.state

import android.os.Parcelable
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.ViewState
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskDetailViewState(
    //originalTask is used to determine we should show edit button or not(used in combine flow)
    //if use update task it will not reflect on originalTask task but task will change(for
    var originalTask: Task? = null,
    //task determine task in view
    var task: Task? = null,

    var isUpdatePending: Boolean? = null
) : Parcelable, ViewState {
}