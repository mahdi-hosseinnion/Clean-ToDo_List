package com.example.clean_todo_list.util

import android.util.Log
import com.example.clean_todo_list.util.Constants.DEBUG
import com.example.clean_todo_list.util.Constants.TAG
import com.google.firebase.crashlytics.FirebaseCrashlytics

var isUnitTest = false

fun printLogD(className: String?, message: String) {
    if (DEBUG && !isUnitTest) {
        Log.d(TAG, "$className: $message")
    } else if (DEBUG && isUnitTest) {
        println("$className: $message")
    }
}
fun printLogE(className: String?, message: String) {
    if (DEBUG && !isUnitTest) {
        Log.e(className, "$message")
    } else if (DEBUG && isUnitTest) {
        println("ERROR IN: $className: $message")
    }
}

fun cLog(message: String?, className: String? = null) {
    message?.let {
        printLogE(TAG, "$className: message: $message ")
        if (!DEBUG) {
            FirebaseCrashlytics.getInstance().log(className.toString() + ": " + message)
            FirebaseCrashlytics.getInstance().recordException(RuntimeException(message))
        }
    }
}

