package com.example.clean_todo_list.di

import androidx.room.Room
import com.example.clean_todo_list.framework.datasource.cache.database.TaskDataBase
import com.example.clean_todo_list.framework.presentation.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object TestModule {


    @JvmStatic
    @Singleton
    @Provides
    fun provideTaskDataBase(app: TestBaseApplication): TaskDataBase {
        return Room
            .inMemoryDatabaseBuilder(app, TaskDataBase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

}