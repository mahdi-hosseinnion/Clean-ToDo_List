package com.example.clean_todo_list.framework.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.clean_todo_list.framework.datasource.cache.model.TaskCacheEntity

@Database(entities = [TaskCacheEntity::class], version = 1)
abstract class TaskDataBase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        val DATABASE_NAME: String = "task_db"
    }
}