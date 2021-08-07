package com.example.clean_todo_list.framework.presentation

import com.example.clean_todo_list.di.DaggerTestAppComponent


class TestBaseApplication : BaseApplication() {

    override fun initAppComponent() {
        appComponent = DaggerTestAppComponent
            .factory()
            .create(this)
    }
}