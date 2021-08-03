package com.example.clean_todo_list.business.data.network.abstraction

import com.example.clean_todo_list.business.domain.model.Task

interface TaskNetworkDataSource {

    suspend fun insertOrUpdateTask(task: Task)

    suspend fun deleteTask(primaryKey: String)

    suspend fun insertDeletedTask(task: Task)

    suspend fun insertDeletedTasks(tasks: List<Task>)

    suspend fun deleteDeletedTask(task: Task)

    suspend fun deleteDeletedTasks(tasks: List<Task>)

    suspend fun getDeletedTasks(): List<Task>

    suspend fun deleteAllTasks()

    suspend fun searchTask(task: Task): Task?

    suspend fun getAllTasks(): List<Task>

    suspend fun insertOrUpdateTasks(tasks: List<Task>)

    suspend fun updateIsDone(
        taskId: String,
        isDone: Boolean
    )

}