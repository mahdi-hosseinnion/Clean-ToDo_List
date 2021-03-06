package com.example.clean_todo_list.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.clean_todo_list.framework.datasource.cache.database.TaskDataBase
import com.example.clean_todo_list.framework.datasource.prefrences.PreferenceKeys
import com.example.clean_todo_list.framework.presentation.BaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ProductionModule {


    @JvmStatic
    @Singleton
    @Provides
    fun provideTaskDb(app: BaseApplication): TaskDataBase {
        return Room
            .databaseBuilder(app, TaskDataBase::class.java, TaskDataBase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPreferences(
        application: BaseApplication
    ): SharedPreferences {
        return application
            .getSharedPreferences(
                PreferenceKeys.TASK_PREFERENCES,
                Context.MODE_PRIVATE
            )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPreferencesEditor(
        sharedPreferences: SharedPreferences
    ): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }


}