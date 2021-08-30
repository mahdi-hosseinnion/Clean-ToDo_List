package com.example.clean_todo_list.di

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.cache.implementation.TaskCacheDataSourceImpl
import com.example.clean_todo_list.business.data.network.auth.abstraction.AuthNetworkDataSource
import com.example.clean_todo_list.business.data.network.auth.implementation.AuthNetworkDataSourceImpl
import com.example.clean_todo_list.business.data.network.task.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.data.network.task.implementation.TaskNetworkDataSourceImpl
import com.example.clean_todo_list.business.interactors.common.ChangeTaskDoneState
import com.example.clean_todo_list.business.interactors.common.DeleteTask
import com.example.clean_todo_list.business.interactors.splash.SyncDeletedTasks
import com.example.clean_todo_list.business.interactors.splash.SyncTasks
import com.example.clean_todo_list.business.interactors.task.taskdetail.TaskDetailInteractors
import com.example.clean_todo_list.business.interactors.task.taskdetail.UpdateTask
import com.example.clean_todo_list.business.interactors.task.tasklist.*
import com.example.clean_todo_list.framework.datasource.cache.abstraction.TaskDaoService
import com.example.clean_todo_list.framework.datasource.cache.database.TaskDao
import com.example.clean_todo_list.framework.datasource.cache.database.TaskDataBase
import com.example.clean_todo_list.framework.datasource.cache.implementation.TaskDaoServiceImpl
import com.example.clean_todo_list.framework.datasource.network.auth.abstraction.AuthFirebaseService
import com.example.clean_todo_list.framework.datasource.network.auth.implementation.AuthFirebaseServiceImpl
import com.example.clean_todo_list.framework.datasource.network.task.abstraction.TaskFirestoreService
import com.example.clean_todo_list.framework.datasource.network.task.implemetation.TaskFirestoreServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Module
object AppModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideTaskDao(taskDataBase: TaskDataBase): TaskDao {
        return taskDataBase.taskDao()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTaskDaoService(
        taskDao: TaskDao
    ): TaskDaoService {
        return TaskDaoServiceImpl(taskDao)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTaskCacheDataSource(
        taskDaoService: TaskDaoService
    ): TaskCacheDataSource {
        return TaskCacheDataSourceImpl(taskDaoService)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirestoreService(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): TaskFirestoreService {
        return TaskFirestoreServiceImpl(
            firebaseFirestore,
            firebaseAuth
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTaskNetworkDataSource(
        firestoreService: TaskFirestoreService
    ): TaskNetworkDataSource {
        return TaskNetworkDataSourceImpl(
            firestoreService
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideAuthFirebaseService(
        firebaseAuth: FirebaseAuth
    ): AuthFirebaseService {
        return AuthFirebaseServiceImpl(
            firebaseAuth
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideAuthNetworkDataSource(
        authFirebaseService: AuthFirebaseService
    ): AuthNetworkDataSource {
        return AuthNetworkDataSourceImpl(
            authFirebaseService
        )
    }

    //TODO WHY DID NOT JUST INJECT THIS
    @JvmStatic
    @Singleton
    @Provides
    fun provideSyncTasks(
        taskCacheDataSource: TaskCacheDataSource,
        taskNetworkDataSource: TaskNetworkDataSource
    ): SyncTasks {
        return SyncTasks(
            taskCacheDataSource,
            taskNetworkDataSource
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSyncDeletedTasks(
        taskCacheDataSource: TaskCacheDataSource,
        taskNetworkDataSource: TaskNetworkDataSource
    ): SyncDeletedTasks {
        return SyncDeletedTasks(
            taskCacheDataSource,
            taskNetworkDataSource
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTaskDetailInteractors(
        taskCacheDataSource: TaskCacheDataSource,
        taskNetworkDataSource: TaskNetworkDataSource
    ): TaskDetailInteractors {
        return TaskDetailInteractors(
            DeleteTask(taskCacheDataSource, taskNetworkDataSource),
            ChangeTaskDoneState(taskCacheDataSource, taskNetworkDataSource),
            UpdateTask(taskCacheDataSource, taskNetworkDataSource)
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTaskListInteractors(
        taskCacheDataSource: TaskCacheDataSource,
        taskNetworkDataSource: TaskNetworkDataSource
    ): TaskListInteractors {
        return TaskListInteractors(
            InsertNewTask(taskCacheDataSource, taskNetworkDataSource),
            DeleteTask(taskCacheDataSource, taskNetworkDataSource),
            ObserveTaskInCache(taskCacheDataSource),
            GetNumTasks(taskCacheDataSource),
            RestoreDeletedTask(taskCacheDataSource, taskNetworkDataSource),
            DeleteMultipleTask(taskCacheDataSource, taskNetworkDataSource),
            ChangeTaskDoneState(taskCacheDataSource, taskNetworkDataSource)
        )
    }


}
