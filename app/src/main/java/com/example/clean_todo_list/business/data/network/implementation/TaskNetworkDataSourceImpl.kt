package com.example.clean_todo_list.business.data.network.implementation

import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.framework.datasource.network.abstraction.TaskFirestoreService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskNetworkDataSourceImpl
@Inject
constructor(
    private val firestoreService: TaskFirestoreService
) : TaskNetworkDataSource {

    override suspend fun insertTask(task: Task) {
        firestoreService.insertTask(task)
    }

    override suspend fun insertTasks(tasks: List<Task>) {
        firestoreService.insertTasks(tasks)
    }

    override suspend fun updateTask(task: Task, updated_at: Long) {
        firestoreService.updateTask(task, updated_at)
    }


    override suspend fun deleteTask(primaryKey: String) =
        firestoreService.deleteTask(primaryKey)

    override suspend fun insertDeletedTask(task: Task) =
        firestoreService.insertDeletedTask(task)

    override suspend fun insertDeletedTasks(tasks: List<Task>) =
        firestoreService.insertDeletedTasks(tasks)

    override suspend fun deleteDeletedTask(task: Task) =
        firestoreService.deleteDeletedTask(task)

    override suspend fun deleteDeletedTasks(tasks: List<Task>) =
        firestoreService.deleteDeletedTasks(tasks)

    override suspend fun getDeletedTasks(): List<Task> =
        firestoreService.getDeletedTasks()

    override suspend fun deleteAllTasks() =
        firestoreService.deleteAllTasks()

    override suspend fun searchTask(task: Task): Task? =
        firestoreService.searchTask(task)

    override suspend fun getAllTasks(): List<Task> =
        firestoreService.getAllTasks()


    override suspend fun updateIsDone(taskId: String, isDone: Boolean) {
        firestoreService.updateIsDone(taskId, isDone)
    }
}