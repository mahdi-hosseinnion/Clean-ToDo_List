package com.example.clean_todo_list.framework.datasource.cache.abstraction

import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.framework.datasource.cache.database.TaskDao.Companion.TASK_PAGINATION_PAGE_SIZE
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder
import kotlinx.coroutines.flow.Flow

interface TaskDaoService {
    suspend fun insertTask(task: Task): Long

    suspend fun deleteTask(primaryKey: String): Int

    suspend fun deleteTasks(tasks: List<Task>): Int

    suspend fun updateTask(
        primaryKey: String,
        newTitle: String,
        newBody: String,
        newIsDone: Boolean,
        updated_at: Long
    ): Int

    suspend fun getAllTasks(): List<Task>

    suspend fun searchTaskById(primaryKey: String): Task?

    suspend fun getNumOfTasks(): Int

    suspend fun insertTasks(tasks: List<Task>): LongArray

    suspend fun updateIsDone(primaryKey: String, isDone: Boolean): Int

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


    suspend fun returnOrderedQuery(
        query: String,
        sortAndOrder: SortAndOrder,
        page: Int
    ): List<Task>

    fun observeOrderedQuery(
        query: String,
        sortAndOrder: SortAndOrder,
        page: Int
    ): Flow<List<Task>>
}