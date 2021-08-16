package com.example.clean_todo_list.framework.datasource.network.abstraction

import com.example.clean_todo_list.business.domain.model.Task

interface TaskFirestoreService {

    suspend fun insertTask(task: Task)

    suspend fun insertTasks(tasks: List<Task>)

    suspend fun updateTask(task: Task, updated_at: Long)

    suspend fun deleteTask(primaryKey: String)

    suspend fun insertDeletedTask(task: Task)

    suspend fun insertDeletedTasks(tasks: List<Task>)

    suspend fun deleteDeletedTask(task: Task)

    suspend fun deleteDeletedTasks(tasks: List<Task>)

    suspend fun getDeletedTasks(): List<Task>

    suspend fun deleteAllTasks()

    suspend fun searchTask(task: Task): Task?

    suspend fun getAllTasks():List<Task>

    suspend fun updateIsDone(taskId: String, isDone: Boolean)
}