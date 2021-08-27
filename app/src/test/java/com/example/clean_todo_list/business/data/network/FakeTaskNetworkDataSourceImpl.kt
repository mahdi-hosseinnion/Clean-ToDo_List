package com.example.clean_todo_list.business.data.network

import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.util.DateUtil

class FakeTaskNetworkDataSourceImpl(
    private val tasksData: HashMap<String, Task>,
    private val deletedTasksData: HashMap<String, Task>,
    private val errorCases: List<String> = emptyList()
) : TaskNetworkDataSource {

    override suspend fun insertTask(task: Task) {
        tasksData[task.id] = task
    }

    override suspend fun insertTasks(tasks: List<Task>) {
        for (task in tasks) {
            tasksData[task.id] = task
        }
    }

    override suspend fun updateTask(task: Task, updated_at: Long) {
        tasksData[task.id] = task.copy(
            updated_at = updated_at
        )
    }

    override suspend fun deleteTask(primaryKey: String) {
        tasksData.remove(primaryKey)
    }

    override suspend fun insertDeletedTask(task: Task) {
        deletedTasksData[task.id] = task
    }

    override suspend fun insertDeletedTasks(tasks: List<Task>) {
        for (task in tasks) {
            deletedTasksData[task.id] = task
        }
    }

    override suspend fun deleteDeletedTask(task: Task) {
        deletedTasksData.remove(task.id)
    }

    override suspend fun deleteDeletedTasks(tasks: List<Task>) {
        for (task in tasks) {
            deletedTasksData.remove(task.id)
        }
    }

    override suspend fun getDeletedTasks(): List<Task> {
        if (errorCases.contains("getDeletedTasks:Exception")) {
            throw Exception("Requested exception")
        }
        return ArrayList(deletedTasksData.values)
    }

    override suspend fun deleteAllTasks() {
        tasksData.clear()
    }

    override suspend fun searchTask(task: Task): Task? {
        return tasksData[task.id]
    }

    override suspend fun getAllTasks(): List<Task> {
        return ArrayList(tasksData.values)
    }

    override suspend fun updateIsDone(taskId: String, isDone: Boolean) {
        tasksData[taskId]?.let {
            tasksData[taskId] = it.copy(isDone = isDone)
        }
    }
}