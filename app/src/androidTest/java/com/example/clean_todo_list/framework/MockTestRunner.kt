package com.example.clean_todo_list.framework

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.example.clean_todo_list.framework.presentation.TestBaseApplication


class MockTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(
            cl,
            TestBaseApplication::class.java.name,
            context
        )
    }
}