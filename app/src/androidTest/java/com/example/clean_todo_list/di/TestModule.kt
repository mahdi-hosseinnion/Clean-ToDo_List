package com.example.clean_todo_list.di

import android.app.Application
import androidx.room.Room
import com.example.clean_todo_list.framework.datasource.cache.database.TaskDataBase
import com.example.clean_todo_list.framework.datasource.data.TaskDataFactory
import com.example.clean_todo_list.framework.presentation.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

//firestore docs
//https://firebase.google.com/docs/emulator-suite/connect_and_prototype#locally_initialize_a_firebase_project
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
    fun provideFirestoreSetting(): FirebaseFirestoreSettings =
        FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()


    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(settings: FirebaseFirestoreSettings): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = settings
        firestore.useEmulator("10.0.2.2", 8080)
        return firestore
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTaskDataFactory(application: TestBaseApplication): TaskDataFactory {
        return TaskDataFactory(application)
    }

}