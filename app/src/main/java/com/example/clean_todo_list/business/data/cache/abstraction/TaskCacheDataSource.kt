package com.example.clean_todo_list.business.data.cache.abstraction

import com.example.clean_todo_list.business.domain.model.Task

interface TaskCacheDataSource {

    suspend fun insertTask(task: Task): Long

    suspend fun deleteTask(primaryKey: String): Int

    suspend fun deleteTasks(tasks: List<Task>): Int

    suspend fun updateTask(
        primaryKey: String,
        newTitle: String,
        newBody: String,
        newIsDone: Boolean
    ): Int

    suspend fun searchTask(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Task>

    suspend fun searchTaskById(primaryKey: String): Task?

    suspend fun getNumOfTasks(): Int

    suspend fun insertTasks(tasks: List<Task>): LongArray

    suspend fun updateIsDone(primaryKey: String, isDone: Boolean): Int //set isDone to true
}