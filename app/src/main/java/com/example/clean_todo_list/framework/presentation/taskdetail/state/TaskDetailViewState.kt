package com.example.clean_todo_list.framework.presentation.taskdetail.state

import android.os.Parcelable
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.ViewState
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskDetailViewState(
    var task: Task? = null,

    var isUpdatePending: Boolean? = null
) : Parcelable , ViewState {
}