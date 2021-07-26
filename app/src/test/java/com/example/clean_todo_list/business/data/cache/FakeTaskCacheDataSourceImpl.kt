package com.example.clean_todo_list.business.data.cache

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.util.DateUtil
import com.example.clean_todo_list.framework.datasource.database.TASK_PAGINATION_PAGE_SIZE

private const val TAG = "FakeTaskCacheDataSource"
private const val FORCE_EXCEPTION = "FORCE_EXCEPTION"
private const val FORCE_GENERAL_FAILURE = "FORCE_GENERAL_FAILURE"

class FakeTaskCacheDataSourceImpl(
    private val tasksData: HashMap<String, Task>
) : TaskCacheDataSource {

    override suspend fun insertTask(task: Task): Long {
        if (task.id == FORCE_EXCEPTION) {
            throw Exception("insertTask error")
        } else if (task.id == FORCE_GENERAL_FAILURE) {
            return -1//fail
        }
        tasksData[task.id] = task
        return 1//success
    }

    override suspend fun deleteTask(primaryKey: String): Int {
        if (primaryKey == FORCE_EXCEPTION) {
            throw Exception("deleteTask error")
        } else if (primaryKey == FORCE_GENERAL_FAILURE) {
            throw Exception("deleteTask error")
        }
        return tasksData.remove(primaryKey)?.let {
            1//success
        } ?: -1//fail
    }

    override suspend fun deleteTasks(tasks: List<Task>): Int {
        var failOrSuccess = 1
        for (task in tasks) {
            if (tasksData.remove(task.id) == null) {
                failOrSuccess = -1 // mark for failure
            }
        }
        return failOrSuccess
    }

    override suspend fun updateTask(
        primaryKey: String,
        newTitle: String,
        newBody: String,
        newIsDone: Boolean
    ): Int {
        if (primaryKey == FORCE_EXCEPTION) {
            throw Exception("updateTask error")
        } else if (primaryKey == FORCE_GENERAL_FAILURE) {
            throw Exception("updateTask error")
        }
        val updatedTask = Task(
            id = primaryKey,
            title = newTitle,
            body = newBody,
            isDone = newIsDone,
            updated_at = DateUtil.getCurrentTimestamp(),
            created_at = tasksData[primaryKey]?.created_at ?: DateUtil.getCurrentTimestamp()
        )
        return tasksData.replace(primaryKey, updatedTask)?.let {
            1//success
        } ?: -1//fail
    }

    // Not testing the order/filter. Just basic query
    // simulate SQLite "LIKE" query on title and body
    override suspend fun searchTask(query: String, filterAndOrder: String, page: Int): List<Task> {
        if (query == FORCE_EXCEPTION) {
            throw Exception("Something went searching the cache for tasks.")
        }
        val results: ArrayList<Task> = ArrayList()
        for (task in tasksData.values) {
            if (task.title.contains(query)) {
                results.add(task)
            } else if (task.body.contains(query)) {
                results.add(task)
            }
            if (results.size > (page * TASK_PAGINATION_PAGE_SIZE)) {
                break
            }
        }
        return results
    }

    override suspend fun searchTaskById(primaryKey: String): Task? {
        if (primaryKey == FORCE_EXCEPTION) {
            throw Exception("searchTaskById error")
        } else if (primaryKey == FORCE_GENERAL_FAILURE) {
            null
        }
        return tasksData[primaryKey]
    }

    override suspend fun getNumOfTasks(): Int {
        return tasksData.size
    }

    override suspend fun insertTasks(tasks: List<Task>): LongArray {
        val results = LongArray(tasks.size)
        for ((index, task) in tasks.withIndex()) {
            results[index] = 1
            tasksData[task.id] = task
        }
        return results
    }

    override suspend fun updateIsDone(primaryKey: String, isDone: Boolean): Int {
        if (primaryKey == FORCE_EXCEPTION) {
            throw Exception("insertTask error")
        } else if (primaryKey == FORCE_GENERAL_FAILURE) {
            throw Exception("insertTask error")
        }
        val updatedTask = tasksData[primaryKey]?.copy(
            isDone = isDone
        ) ?: return -1

        return tasksData.replace(primaryKey, updatedTask)?.let {
            1//success
        } ?: -1//fail
    }
}