package com.example.clean_todo_list.business.interactors.splash

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.util.printLogD
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 * testCases:
 * 1. syncDeletedTask_success_confirmTasksDeletedInCache()
 *      a) select handful of tasks and delete them from cache then add them into 'deleted' node in
 *          network (simulate delete state)
 *      b) perform syncDeletedTask
 *      c) confirm task were deleted in network
 */
class SyncDeletedTasksTest {
    //system under test
    private val syncDeletedTasks: SyncDeletedTasks

    // dependencies
    private val dependencyContainer = DependencyContainer()
    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    private val taskNetworkDataSource: TaskNetworkDataSource =
        dependencyContainer.taskNetworkDataSource

    init {
        syncDeletedTasks = SyncDeletedTasks(
            taskCacheDataSource = taskCacheDataSource,
            taskNetworkDataSource = taskNetworkDataSource
        )
    }

    @Test
    fun syncDeletedTask_success_confirmTasksDeletedInCache() = runBlocking {

        val allTasksInNetwork = taskNetworkDataSource.getAllTasks()
        val randomTasksToDelete = allTasksInNetwork.filter { Random.nextBoolean() }
        printLogD(
            "sync deleted notes",
            "delete ${randomTasksToDelete.size} task from total ${allTasksInNetwork.size} task"
        )
        for (item in randomTasksToDelete) {
            taskNetworkDataSource.deleteTask(item.id)
        }
        taskNetworkDataSource.insertDeletedTasks(randomTasksToDelete)

        for (task in taskNetworkDataSource.getDeletedTasks()) {
            //confirm tasks were actually insert into 'deleted' node (false-positive)
            assertTrue { randomTasksToDelete.contains(task) }
        }
        for (task in randomTasksToDelete) {
            //confirm tasks actually exist in cache (false-positive)
            assertEquals(
                task,
                taskCacheDataSource.searchTaskById(task.id)
            )
        }

        syncDeletedTasks.syncDeletedTasks()

        for (item in randomTasksToDelete) {
            //confirm tasks deleted from cache
            assertNull(
                taskCacheDataSource.searchTaskById(item.id)
            )
        }


    }
}