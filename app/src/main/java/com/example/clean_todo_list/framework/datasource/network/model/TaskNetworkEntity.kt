package com.example.clean_todo_list.framework.datasource.network.model

import com.google.firebase.Timestamp

data class TaskNetworkEntity(
    var id: String,
    var title: String,
    var body: String,
    var isDone: Boolean,
    var updated_at: Timestamp,
    var created_at: Timestamp
) {
    //no args constructor for firebase
    constructor() : this(
        "",
        "",
        "",
        false,
        Timestamp.now(),
        Timestamp.now()
    )
}