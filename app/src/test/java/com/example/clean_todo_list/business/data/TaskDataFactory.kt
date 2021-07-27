package com.example.clean_todo_list.business.data

import com.example.clean_todo_list.business.domain.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskDataFactory(
    private val testClassLoader: ClassLoader
) {

    fun produceListOfTasks(): List<Task> {
        val tasks: List<Task> = Gson()
            .fromJson(
                convertJsonFileToString("task_list.json"),
                object : TypeToken<List<Task>>() {}.type
            )
        return tasks
    }

    fun produceHashmapOfTasks(tasks: List<Task>): HashMap<String, Task> {
        val result = HashMap<String, Task>()
        for (item in tasks) {
            result.put(item.id, item)
        }
        return result
    }

    fun produceEmptyListOfTasks(): List<Task> {
        return ArrayList()
    }

    fun convertJsonFileToString(fileName: String): String {
        return testClassLoader.getResource(fileName).readText()
    }
}