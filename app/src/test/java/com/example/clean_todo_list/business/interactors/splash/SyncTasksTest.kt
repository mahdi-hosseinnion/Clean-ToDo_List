package com.example.clean_todo_list.business.interactors.splash

import android.util.Log
import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.domain.util.DateUtil
import com.example.clean_todo_list.di.DependencyContainer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.random.Random


/**
 * 1. syncTask_insert_addRandomTasksToCacheThenSyncAndConfirmNetworkUpdated()
 *      a) insert bunch of tasks into cache
 *      b) perform sync
 *      c) confirm tasks inserted into network
 *
 * 2. syncTask_insert_addRandomTaskToNetworkThenSyncAndConfirmCacheUpdated()
 *      a) insert bunch of tasks intro network
 *      b) perform sync
 *      c) confirm tasks inserted into cache
 *
 * 3. syncTask_update_updateRandomTasksInCacheThenSyncAndConfirmNetworkUpdated()
 *      a) select random task from cache
 *      b) update them
 *      c) perform sync
 *      d) confirm tasks updated in network
 *
 * 4. syncTask_update_updateRandomTaskInNetworkThenSyncAndConfirmCacheUpdated()
 *      a) select random task from network
 *      b) update them
 *      c) perform sync
 *      d) confirm tasks updated in cache
 */
class SyncTasksTest {

    //system under test
    private val syncTasks: SyncTasks

    // dependencies
    private val dependencyContainer = DependencyContainer()
    private val taskCacheDataSource: TaskCacheDataSource =
        dependencyContainer.taskCacheDataSource

    private val taskNetworkDataSource: TaskNetworkDataSource =
        dependencyContainer.taskNetworkDataSource

    init {
        syncTasks = SyncTasks(
            taskCacheDataSource = taskCacheDataSource,
            taskNetworkDataSource = taskNetworkDataSource
        )
    }

    @Test
    fun syncTask_insert_addRandomTasksToCacheThenSyncAndConfirmNetworkUpdated() = runBlocking {

        val randomTasks = TaskFactory.createListOfTask(50)

        taskCacheDataSource.insertTasks(randomTasks)

        syncTasks.syncTasks()

        for (task in randomTasks) {
            //confirm tasks actually exist in cache (avoid false-positive)
            assertEquals(
                task,
                taskCacheDataSource.searchTaskById(task.id)
            )

            assertEquals(
                task,
                taskNetworkDataSource.searchTask(task)
            )
        }
    }

    @Test
    fun syncTask_insert_addRandomTaskToNetworkThenSyncAndConfirmCacheUpdated() = runBlocking {

        val randomTasks = TaskFactory.createListOfTask(50)

        taskNetworkDataSource.insertOrUpdateTasks(randomTasks)

        syncTasks.syncTasks()

        for (task in randomTasks) {
            //confirm tasks actually exist in cache (avoid false-positive)
            assertEquals(
                task,
                taskNetworkDataSource.searchTask(task)
            )

            assertEquals(
                task,
                taskCacheDataSource.searchTaskById(task.id)
            )
        }
    }

    @Test
    fun syncTask_update_updateRandomTasksInCacheThenSyncAndConfirmNetworkUpdated() = runBlocking {

        val allTasksInCache = taskCacheDataSource.getAllTasks()

        val bunchOfRandomTasksInCache: List<Task> = allTasksInCache.filter { Random.nextBoolean() }

        val updatedTasks = ArrayList<Task>()

        for (task in bunchOfRandomTasksInCache) {
            val taskToUpdate = Task(
                id = task.id,
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString(),
                isDone = Random.nextBoolean(),
                updated_at = DateUtil.getCurrentTimestamp(),
                created_at = task.created_at
            )
            taskCacheDataSource.updateTask(
                taskToUpdate.id,
                taskToUpdate.title,
                taskToUpdate.body,
                taskToUpdate.isDone
            )
            updatedTasks.add(taskToUpdate)
        }

        syncTasks.syncTasks()

        for (task in updatedTasks) {
            //confirm tasks actually updated in cache (avoid false-positive)
            assertEquals(
                task,
                taskCacheDataSource.searchTaskById(task.id)
            )

            assertEquals(
                task,
                taskNetworkDataSource.searchTask(task)
            )
        }
    }

    @Test
    fun syncTask_update_updateRandomTaskInNetworkThenSyncAndConfirmCacheUpdated() = runBlocking {

        val allTasksInNetwork = taskNetworkDataSource.getAllTasks()

        val bunchOfRandomTasksFromNetwork: List<Task> =
            allTasksInNetwork.filter { Random.nextBoolean() }

        val updatedTasks = ArrayList<Task>()

        for (task in bunchOfRandomTasksFromNetwork) {
            val taskToUpdate = Task(
                id = task.id,
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString(),
                isDone = Random.nextBoolean(),
                updated_at = DateUtil.getCurrentTimestamp(),
                created_at = task.created_at
            )
            taskNetworkDataSource.insertOrUpdateTask(
                taskToUpdate
            )
            updatedTasks.add(taskToUpdate)
        }

        syncTasks.syncTasks()

        for (task in updatedTasks) {
            //confirm tasks actually updated in cache (avoid false-positive)
            assertEquals(
                task,
                taskNetworkDataSource.searchTask(task)
            )

            assertEquals(
                task,
                taskCacheDataSource.searchTaskById(task.id)

            )
        }
    }


}