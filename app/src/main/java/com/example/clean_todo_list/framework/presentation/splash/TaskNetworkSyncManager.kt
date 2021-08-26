package com.example.clean_todo_list.framework.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.clean_todo_list.business.domain.state.MessageType
import com.example.clean_todo_list.business.interactors.splash.SyncDeletedTasks
import com.example.clean_todo_list.business.interactors.splash.SyncTasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskNetworkSyncManager
@Inject
constructor(
    private val syncTasks: SyncTasks,
    private val syncDeletedTasks: SyncDeletedTasks
) {
    private val _hasSyncBeenExecuted: MutableLiveData<Boolean> = MutableLiveData(false)

    val hasSyncBeenExecuted: LiveData<Boolean> get() = _hasSyncBeenExecuted

    fun executeSync(coroutineScope: CoroutineScope) {
        if (hasSyncBeenExecuted.value == true) {
            return
        }

        val syncJob = coroutineScope.launch {
            try {
                withTimeout(TaskNetworkSyncTimeout) {
                    val deleteJob = syncDeletedTasks.syncDeletedTasks()

                    if (deleteJob?.stateMessage?.response?.messageType == MessageType.Success) {
                        syncTasks.syncTasks()
                    }

                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                when (throwable) {
                    is TimeoutCancellationException -> {
                        //HANDLE this
                        //TODO NOTIFY USER
//                        CacheResult.GenericError(CacheErrors.CACHE_ERROR_TIMEOUT)
                    }
                }
            }

        }
        syncJob.invokeOnCompletion {
            coroutineScope.launch(Main) {
                _hasSyncBeenExecuted.value = true
            }
        }

    }

    companion object {
        private const val TAG = "TaskNetworkSyncManager"
        private const val TaskNetworkSyncTimeout: Long = 10_000
    }
}