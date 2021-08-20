package com.example.clean_todo_list.framework.presentation.common

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clean_todo_list.business.interactors.taskdetail.TaskDetailInteractors
import com.example.clean_todo_list.business.interactors.tasklist.TaskListInteractors
import com.example.clean_todo_list.framework.presentation.splash.SplashViewModel
import com.example.clean_todo_list.framework.presentation.taskdetail.TaskDetailViewModel
import com.example.clean_todo_list.framework.presentation.taskdetail.TaskListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class TaskViewModelFactory
@Inject
constructor(
    private val taskListInteractors: TaskListInteractors,
    private val taskDetailInteractors: TaskDetailInteractors,
    private val sharedPreferences: SharedPreferences,
    private val sharedPrefsEditor: SharedPreferences.Editor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {

            TaskListViewModel::class.java -> {
                TaskListViewModel(
                    taskListInteractors = taskListInteractors,
                    sharedPreferences = sharedPreferences,
                    sharedPrefsEditor = sharedPrefsEditor
                ) as T
            }

            TaskDetailViewModel::class.java -> {
                TaskDetailViewModel(
                    taskDetailInteractors = taskDetailInteractors
                ) as T
            }

            SplashViewModel::class.java -> {
                SplashViewModel() as T
            }

            else -> {
                throw IllegalArgumentException("unknown model class $modelClass")
            }
        }
    }
}
