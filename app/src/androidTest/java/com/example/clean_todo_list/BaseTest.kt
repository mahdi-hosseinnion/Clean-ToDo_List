package com.example.clean_todo_list

import androidx.test.core.app.ApplicationProvider
import com.example.clean_todo_list.framework.presentation.TestBaseApplication

abstract class BaseTest {

    val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext() as TestBaseApplication

    init {
        inject()
    }

    abstract fun inject()
}