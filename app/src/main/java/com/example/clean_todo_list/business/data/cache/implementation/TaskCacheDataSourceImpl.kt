package com.example.clean_todo_list.business.data.cache.implementation

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.framework.datasource.cache.abstraction.TaskDaoService
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskCacheDataSourceImpl
@Inject
constructor(
    private val taskDaoService: TaskDaoService
) : TaskCacheDataSource {


    override suspend fun insertTask(task: Task): Long =
        taskDaoService.insertTask(task)

    override suspend fun deleteTask(primaryKey: String): Int =
        taskDaoService.deleteTask(primaryKey)

    override suspend fun deleteTasks(tasks: List<Task>): Int =
        taskDaoService.deleteTasks(tasks)

    override suspend fun updateTask(
        primaryKey: String,
        newTitle: String,
        newBody: String,
        newIsDone: Boolean,
        updated_at: Long
    ): Int =
        taskDaoService.updateTask(primaryKey, newTitle, newBody, newIsDone, updated_at)

    override suspend fun searchTask(
        query: String,
        sortAndOrder: SortAndOrder,
        page: Int
    ): List<Task> =
        taskDaoService.returnOrderedQuery(query, sortAndOrder, page)

    override suspend fun observeTasksInCache(
        query: String,
        sortAndOrder: SortAndOrder,
        page: Int
    ): Flow<List<Task>> =
        taskDaoService.observeOrderedQuery(query, sortAndOrder, page)


    override suspend fun getAllTasks(): List<Task> =
        taskDaoService.getAllTasks()


    override suspend fun searchTaskById(primaryKey: String): Task? =
        taskDaoService.searchTaskById(primaryKey)

    override suspend fun getNumOfTasks(): Int =
        taskDaoService.getNumOfTasks()

    override suspend fun insertTasks(tasks: List<Task>): LongArray =
        taskDaoService.insertTasks(tasks)

    override suspend fun updateIsDone(primaryKey: String, isDone: Boolean): Int =
        taskDaoService.updateIsDone(primaryKey, isDone)
}



