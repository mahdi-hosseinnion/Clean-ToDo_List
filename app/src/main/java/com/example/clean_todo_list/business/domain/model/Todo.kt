package com.example.clean_todo_list.business.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Todo(
    val id: String,
    val title: String,
    val body: String,
    val isDone: Boolean,
    val updated_at: Long,//unix timestamp in  second
    val created_at: Long
) : Parcelable
