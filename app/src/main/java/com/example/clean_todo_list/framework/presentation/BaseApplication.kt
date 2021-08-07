package com.example.clean_todo_list.framework.presentation

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.example.clean_todo_list.di.AppComponent
import com.example.clean_todo_list.di.DaggerAppComponent

//https://developer.android.com/studio/build/multidex
open class BaseApplication : MultiDexApplication() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    open fun initAppComponent() {
        appComponent = DaggerAppComponent
            .factory()
            .create(
                this
            )
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


}