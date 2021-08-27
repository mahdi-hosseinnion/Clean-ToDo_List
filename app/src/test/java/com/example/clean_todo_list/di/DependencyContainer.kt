package com.example.clean_todo_list.di

import com.example.clean_todo_list.business.data.TaskDataFactory
import com.example.clean_todo_list.business.data.cache.FakeTaskCacheDataSourceImpl
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.FakeTaskNetworkDataSourceImpl
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.util.isUnitTest

class DependencyContainer {

    val taskCacheDataSource: TaskCacheDataSource
    val taskNetworkDataSource: TaskNetworkDataSource
    lateinit var taskDataFactory: TaskDataFactory

    // data sets
    private lateinit var tasksData: HashMap<String, Task>

    init {
        isUnitTest = true
        this.javaClass.classLoader?.let {
            taskDataFactory = TaskDataFactory(it)
            tasksData =
                taskDataFactory.produceHashmapOfTasks(taskDataFactory.produceListOfTasks())
        }
        taskCacheDataSource = FakeTaskCacheDataSourceImpl(
            tasksData
        )
        taskNetworkDataSource = FakeTaskNetworkDataSourceImpl(
            tasksData = HashMap(tasksData),
            deletedTasksData = HashMap()
        )
    }

    fun newTaskCacheDataSource(errorCases: List<String> = emptyList()): TaskCacheDataSource =
        FakeTaskCacheDataSourceImpl(
            tasksData = HashMap(tasksData),
            errorCases = errorCases
        )

    fun newTaskNetworkDataSource(errorCases: List<String> = emptyList()): TaskNetworkDataSource =
        FakeTaskNetworkDataSourceImpl(
            tasksData = HashMap(tasksData),
            deletedTasksData = HashMap(),
            errorCases = errorCases
        )


}