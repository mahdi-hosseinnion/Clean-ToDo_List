package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.di.DependencyContainer

class InsertNewTaskTest {

    //system under test
    private val insertNewTask: InsertNewTask

    // dependencies
    private val dependencyContainer = DependencyContainer()

    init {
        insertNewTask = InsertNewTask(
            taskCacheDataSource = dependencyContainer.taskCacheDataSource,
            taskNetworkDataSource = dependencyContainer.taskNetworkDataSource
        )
    }
}