package com.example.clean_todo_list.di

import com.example.clean_todo_list.business.data.cache.FakeTaskCacheDataSourceImpl
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.FakeTaskNetworkDataSourceImpl
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.util.isUnitTest

class DependencyContainer {

    val taskCacheDataSource: TaskCacheDataSource
    val taskNetworkDataSource: TaskNetworkDataSource

    init {
        isUnitTest = true

        taskCacheDataSource = FakeTaskCacheDataSourceImpl(HashMap())
        taskNetworkDataSource = FakeTaskNetworkDataSourceImpl(HashMap(), HashMap())
    }


}