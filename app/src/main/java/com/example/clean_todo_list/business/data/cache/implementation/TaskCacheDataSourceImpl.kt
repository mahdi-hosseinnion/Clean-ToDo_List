package com.example.clean_todo_list.business.data.cache.implementation

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.domain.model.Task
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

    override suspend fun deleteTasks(tasks: List<Task>) =
        taskDaoService.deleteTasks(tasks)

    override suspend fun updateTask(
        primaryKey: String,
        newTitle: String,
        newBody: String,
        newIsDone: Boolean
    ): Int =
        taskDaoService.updateTask(primaryKey, newTitle, newBody, newIsDone)

    override suspend fun searchTask(query: String, filterAndOrder: String, page: Int): List<Task> =
        taskDaoService.searchTask(query, filterAndOrder, page)

    override suspend fun searchTaskById(primaryKey: String): Task? =
        taskDaoService.searchTaskById(primaryKey)

    override suspend fun getNumOfTasks(): Int =
        taskDaoService.getNumOfTasks()

    override suspend fun insertTasks(tasks: List<Task>): LongArray =
        taskDaoService.insertTask(tasks)

    override suspend fun doneTask(primaryKey: String): Int =
        taskDaoService.doneTask(primaryKey)
}