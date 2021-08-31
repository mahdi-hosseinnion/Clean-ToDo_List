package com.example.clean_todo_list.framework.datasource.network.model

import com.google.firebase.Timestamp

data class TaskNetworkEntity(
    var id: String,
    var title: String,
    var body: String,
    var done: Boolean,
    var updated_at: Timestamp,
    var created_at: Timestamp
) {
    //no args constructor for firebase
    constructor() : this(
        "",
        "",
        "",
        done = false,
        Timestamp.now(),
        Timestamp.now()
    )
    companion object{
        const val IS_DONE_FIELD  = "done"
        const val UPDATED_AT_FIELD  = "updated_at"
    }
}