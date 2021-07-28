package com.example.clean_todo_list.business.interactors.tasklist

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.interactors.tasklist.GetNumTasks.Companion.GET_NUM_TASKS_SUCCESS
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * 1. getNumberOfTask_success_confirmCorrect
 *  a) get num of tasks in cache
 *  b) observer for GET_NUM_TASKS_SUCCESS in flow
 *  c) compare with the number of tasks in the fake data set
 */
class GetNumTasksTest {

    //system under test
    val getNumTasks: GetNumTasks

    // dependencies
    private val dependencyContainer = DependencyContainer()
    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    init {
        getNumTasks = GetNumTasks(
            taskCacheDataSource = taskCacheDataSource
        )
    }

    @Test
    fun getNumberOfTask_success_confirmCorrect() = runBlocking {

        var result: Int? = null

        getNumTasks.getNumOfTasks(TaskListStateEvent.GetNumTasksInCacheEvent())
            .collect {
                Assertions.assertEquals(
                    GET_NUM_TASKS_SUCCESS,
                    it?.stateMessage?.response?.message
                )
                result = it?.data?.numTasksInCache
            }

        val actualNumberOfTaskInCache = taskCacheDataSource.getNumOfTasks()
        Assertions.assertEquals(actualNumberOfTaskInCache, result)
    }

}