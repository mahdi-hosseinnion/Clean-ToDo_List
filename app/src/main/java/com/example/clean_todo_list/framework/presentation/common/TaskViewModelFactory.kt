package com.example.clean_todo_list.framework.presentation.common

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clean_todo_list.business.interactors.auth.login.LoginUser
import com.example.clean_todo_list.business.interactors.auth.signup.SignUpUser
import com.example.clean_todo_list.business.interactors.task.taskdetail.TaskDetailInteractors
import com.example.clean_todo_list.business.interactors.task.tasklist.TaskListInteractors
import com.example.clean_todo_list.framework.presentation.auth.forgotpassword.ForgotPasswordViewModel
import com.example.clean_todo_list.framework.presentation.auth.login.LogInViewModel
import com.example.clean_todo_list.framework.presentation.auth.signup.SignUpViewModel
import com.example.clean_todo_list.framework.presentation.splash.SplashViewModel
import com.example.clean_todo_list.framework.presentation.splash.TaskNetworkSyncManager
import com.example.clean_todo_list.framework.presentation.task.taskdetail.TaskDetailViewModel
import com.example.clean_todo_list.framework.presentation.task.tasklist.TaskListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TaskViewModelFactory
constructor(
    private val taskListInteractors: TaskListInteractors,
    private val taskDetailInteractors: TaskDetailInteractors,
    private val loginUser: LoginUser,
    private val signUpUser: SignUpUser,
    private val taskNetworkSyncManager: TaskNetworkSyncManager,
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
                SplashViewModel(
                    taskNetworkSyncManager = taskNetworkSyncManager
                ) as T
            }
            LogInViewModel::class.java -> {
                LogInViewModel(
                    loginUser = loginUser
                ) as T

            }
            SignUpViewModel::class.java -> {
                SignUpViewModel(
                    signUpUser
                ) as T

            }
            ForgotPasswordViewModel::class.java -> {
                ForgotPasswordViewModel() as T

            }

            else -> {
                throw IllegalArgumentException("unknown model class $modelClass")
            }
        }
    }
}
