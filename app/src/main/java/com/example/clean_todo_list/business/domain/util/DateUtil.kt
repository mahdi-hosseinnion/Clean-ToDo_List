package com.example.clean_todo_list.business.domain.util

import com.google.firebase.Timestamp
import javax.inject.Inject
import javax.inject.Singleton


object DateUtil {
    fun convertFirebaseTimestampToUnixTimestamp(timeStamp: Timestamp): Long = timeStamp.seconds

    fun convertUnixTimestampToFirebaseTimestamp(unix: Long): Timestamp = Timestamp(unix, 0)

    fun getCurrentTimestamp() = (System.currentTimeMillis()).div(1_000)

}