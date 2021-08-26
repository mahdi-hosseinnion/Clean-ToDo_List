package com.example.clean_todo_list.framework.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplashViewModel
@Inject
constructor(
    private val taskNetworkSyncManager: TaskNetworkSyncManager
) : ViewModel() {

    fun performSync() {
        taskNetworkSyncManager.executeSync(viewModelScope)
    }

    val hasSyncBeenExecuted: LiveData<Boolean> get() = taskNetworkSyncManager.hasSyncBeenExecuted

}