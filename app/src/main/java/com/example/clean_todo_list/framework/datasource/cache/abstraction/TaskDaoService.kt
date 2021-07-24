package com.example.clean_todo_list.framework.datasource.cache.abstraction

import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.framework.datasource.database.TASK_PAGINATION_PAGE_SIZE

interface TaskDaoService {
    suspend fun insertTask(task: Task): Long

    suspend fun deleteTask(primaryKey: String): Int

    suspend fun deleteTasks(tasks: List<Task>)

    suspend fun updateTask(
        primaryKey: String,
        newTitle: String,
        newBody: String,
        newIsDone: Boolean
    ): Int

    suspend fun searchTask(): List<Task>

    suspend fun searchTaskById(primaryKey: String): Task?

    suspend fun getNumOfTasks(): Int

    suspend fun insertTasks(tasks: List<Task>): LongArray

    suspend fun doneTask(primaryKey: String): Int //set isDone to true

    suspend fun searchTasksOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = TASK_PAGINATION_PAGE_SIZE
    ): List<Task>

    suspend fun searchTasksOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = TASK_PAGINATION_PAGE_SIZE
    ): List<Task>

    suspend fun searchTasksOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int = TASK_PAGINATION_PAGE_SIZE
    ): List<Task>

    suspend fun searchTasksOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int = TASK_PAGINATION_PAGE_SIZE
    ): List<Task>

    suspend fun getNumTasks(): Int

    suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Task>
}