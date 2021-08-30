package com.example.clean_todo_list.business.interactors.splash

import com.example.clean_todo_list.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.task.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.MessageType
import com.example.clean_todo_list.business.interactors.splash.SyncDeletedTasks.Companion.THERE_IS_NO_TASK_IN_DELETE_NODE_TO_DELETE
import com.example.clean_todo_list.di.DependencyContainer
import com.example.clean_todo_list.util.printLogD
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 * testCases:
 * 1. syncDeletedTask_success_confirmTasksDeletedInCache()
 *      a) select handful of tasks and delete them from network then add them into 'deleted' node in
 *          network (simulate delete state)
 *      b) perform syncDeletedTask
 *      c) confirm task were deleted in network
 * 2. syncDeletedTask_networkError_confirmTaskDidNotDeleteInCacheAndReturnsError()
 *      a) select handful of tasks and delete them from network then add them into 'deleted' node in
 *          network (simulate delete state)
 *      b) perform syncDeletedTask (force error on network getDeletedTasks fun)
 *      c) confirm tasks were not deleted in network
 * 3. syncDeletedTask_cacheError_confirmTaskDidNotDeleteInCacheAndReturnsError()
 *      a) select handful of tasks and delete them from network then add them into 'deleted' node in
 *          network (simulate delete state)
 *      b) perform syncDeletedTask (force error on cache getDeletedTasks fun)
 *      c) confirm tasks were not deleted in network
 * 4. syncDeletedTask_NoTaskInNetwork_confirmNothingWillDeleted()
 *      a) delete nothing and run with empty network node
 *      b) perform syncDeletedTask
 *      c) confirm THERE_IS_NO_TASK_IN_DELETE_NODE_TO_DELETE message returned
 *      c) confirm cache tasks did not change at all
 *
 * */
class SyncDeletedTasksTest {
    //system under test
    private lateinit var syncDeletedTasks: SyncDeletedTasks

    // dependencies
    private val dependencyContainer = DependencyContainer()
    private var taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    private var taskNetworkDataSource: TaskNetworkDataSource =
        dependencyContainer.taskNetworkDataSource

    @BeforeEach
    fun resetSystemUnderTest() = runBlocking {
        printLogD("resetSystemUnderTest", "BeforeEach")
        taskCacheDataSource = dependencyContainer.newTaskCacheDataSource()
        taskNetworkDataSource = dependencyContainer.newTaskNetworkDataSource()
        syncDeletedTasks = SyncDeletedTasks(
            taskCacheDataSource = taskCacheDataSource,
            taskNetworkDataSource = taskNetworkDataSource
        )

    }

    @Test
    fun syncDeletedTask_success_confirmTasksDeletedInCache() = runBlocking {

        val deletedTasksInNetwork = simulateNetworkDeleteScenario()

        syncDeletedTasks.syncDeletedTasks()

        for (item in deletedTasksInNetwork) {
            //confirm tasks deleted from cache
            assertNull(
                taskCacheDataSource.searchTaskById(item.id)
            )
        }


    }

    @Test
    fun syncDeletedTask_networkError_confirmTaskDidNotDeleteInCacheAndReturns() = runBlocking {

        val deletedTasksInNetwork = simulateNetworkDeleteScenario()
        //force error to throw on getDeletedTasks function
        taskNetworkDataSource =
            dependencyContainer.newTaskNetworkDataSource(listOf("getDeletedTasks:Exception"))
        //inti syncDeleted task with new taskNetworkDataSource
        syncDeletedTasks = SyncDeletedTasks(taskCacheDataSource, taskNetworkDataSource)

        //preform sync
        val result = syncDeletedTasks.syncDeletedTasks()

        assertEquals(MessageType.Error, result?.stateMessage?.response?.messageType)
        assertTrue {
            result?.stateMessage?.response?.message?.contains(SyncDeletedTasks.GET_ALL_TASK_FROM_NETWORK_ERROR)
                ?: false
        }
        //confirm cache did not updated
        for (item in deletedTasksInNetwork) {
            //confirm tasks deleted from cache
            assertNotNull(
                taskCacheDataSource.searchTaskById(item.id)
            )
        }
    }

    @Test
    fun syncDeletedTask_cacheError_confirmTaskDidNotDeleteInCacheAndReturnsError() = runBlocking {

        val deletedTasksInNetwork = simulateNetworkDeleteScenario()
        //force error to throw on getDeletedTasks function
        taskCacheDataSource =
            dependencyContainer.newTaskCacheDataSource(listOf("deleteTasks:Exception"))
        //init syncDeleted task with new taskNetworkDataSource
        syncDeletedTasks = SyncDeletedTasks(taskCacheDataSource, taskNetworkDataSource)

        //preform sync
        val result = syncDeletedTasks.syncDeletedTasks()

        assertEquals(MessageType.Error, result?.stateMessage?.response?.messageType)
        assertTrue {
            result?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN)
                ?: false
        }
        //confirm cache did not updated
        for (item in deletedTasksInNetwork) {
            //confirm tasks deleted from cache
            assertNotNull(
                taskCacheDataSource.searchTaskById(item.id)
            )
        }
    }

    @Test
    fun syncDeletedTask_NoTaskInNetwork_confirmNothingWillDeleted() = runBlocking {
        val taskInCacheBeforeSync = taskCacheDataSource.getAllTasks()
        //confirm theres no task in delete node
        assertTrue(
            taskNetworkDataSource.getDeletedTasks().isEmpty()
        )

        //perform sync
        val result = syncDeletedTasks.syncDeletedTasks()

        assertEquals(MessageType.Success, result?.stateMessage?.response?.messageType)
        assertTrue {
            result?.stateMessage?.response?.message?.contains(
                THERE_IS_NO_TASK_IN_DELETE_NODE_TO_DELETE
            ) ?: false
        }
        //confirm nothing been deleted or changed
        val taskInCacheAfterSync = taskCacheDataSource.getAllTasks()
        assertEquals(taskInCacheBeforeSync, taskInCacheAfterSync)
    }

    private suspend fun simulateNetworkDeleteScenario(): List<Task> {
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
        return randomTasksToDelete
    }
}