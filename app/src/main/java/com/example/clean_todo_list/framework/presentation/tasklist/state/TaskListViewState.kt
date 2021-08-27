package com.example.clean_todo_list.framework.presentation.tasklist.state

import android.os.Parcelable
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.ViewState
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaskListViewState(
    var newTask: Task? = null, //task that can be created with fab and dialog
    var taskPendingDelete: TaskPendingDelete? = null,//set when delete is pending (can be undone)
    var searchQuery: String? = null,
    var page: Int? = null,
    var isQueryExhausted: Boolean? = null,
    val sortAndOrder: SortAndOrder? = null,
    var layoutManagerState: Parcelable? = null,//for process death
    var numTasksInCache: Int? = null
) : Parcelable, ViewState {

    @Parcelize
    data class TaskPendingDelete(
        var task: Task? = null,
        var listPosition: Int? = null
    ) : Parcelable
}