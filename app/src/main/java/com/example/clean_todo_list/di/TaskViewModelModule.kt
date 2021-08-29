package com.example.clean_todo_list.di

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.example.clean_todo_list.business.interactors.auth.login.LoginUser
import com.example.clean_todo_list.business.interactors.task.taskdetail.TaskDetailInteractors
import com.example.clean_todo_list.business.interactors.task.tasklist.TaskListInteractors
import com.example.clean_todo_list.framework.presentation.common.TaskViewModelFactory
import com.example.clean_todo_list.framework.presentation.splash.TaskNetworkSyncManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object TaskViewModelModule {

    @Singleton
    @JvmStatic
    @Provides
    fun provideTaskViewModelFactory(
        taskListInteractors: TaskListInteractors,
        taskDetailInteractors: TaskDetailInteractors,
        taskNetworkSyncManager: TaskNetworkSyncManager,
        loginUser: LoginUser,
        sharedPreferences: SharedPreferences,
        sharedPrefsEditor: SharedPreferences.Editor
    ): ViewModelProvider.Factory {
        return TaskViewModelFactory(
            taskListInteractors = taskListInteractors,
            taskDetailInteractors = taskDetailInteractors,
            taskNetworkSyncManager = taskNetworkSyncManager,
            loginUser = loginUser,
            sharedPreferences = sharedPreferences,
            sharedPrefsEditor = sharedPrefsEditor
        )
    }

}