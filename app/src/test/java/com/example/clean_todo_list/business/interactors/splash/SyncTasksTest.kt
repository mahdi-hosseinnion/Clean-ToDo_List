package com.example.clean_todo_list.business.interactors.splash

import com.example.clean_todo_list.business.data.cache.abstraction.TaskCacheDataSource
import com.example.clean_todo_list.business.data.network.task.abstraction.TaskNetworkDataSource
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.domain.util.DateUtil
import com.example.clean_todo_list.di.DependencyContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
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
    fun doSuccessiveUpdatesOccur() = runBlocking {

        // update a single task with new timestamp
        val newDate = DateUtil.getCurrentTimestamp().minus(Random.nextInt(1_000))
        val allTasksInNetwork = taskNetworkDataSource.getAllTasks()
        val updatedTask =
            allTasksInNetwork.random().copy(
                updated_at = newDate
            )
        taskNetworkDataSource.updateTask(updatedTask, newDate)

        syncTasks.syncTasks()

        delay(1001)

        // simulate launch app again
        syncTasks.syncTasks()

        // confirm the date was not updated a second time
        val tasks = taskNetworkDataSource.getAllTasks()
        for (task in tasks) {
            if (task.id.equals(updatedTask.id)) {
                Assertions.assertTrue { task.updated_at.equals(newDate) }
            }
        }
    }

    @Test
    fun checkUpdatedAtDates() = runBlocking {

        // update a single task with new timestamp
        val newDate = DateUtil.getCurrentTimestamp().minus(Random.nextInt(10, 1_000))
        val allTasksInNetwork = taskNetworkDataSource.getAllTasks()
        val updatedTask =
            allTasksInNetwork.random().copy(
                updated_at = newDate
            )
        taskNetworkDataSource.updateTask(updatedTask, newDate)

        syncTasks.syncTasks()
        // confirm only a single 'updated_at' date was updated
        val tasks = taskNetworkDataSource.getAllTasks()
        for (task in tasks) {
            taskCacheDataSource.searchTaskById(task.id)?.let { n ->
                println("date: ${n.updated_at}")
                if (n.id.equals(updatedTask.id)) {
                    Assertions.assertTrue { n.updated_at.equals(newDate) }
                } else {
                    Assertions.assertFalse { n.updated_at.equals(newDate) }
                }
            }
        }
    }

    @Test
    fun updateTaskInCache_thenSyncTwice() = runBlocking {
        //update a task in cache
        val allTaskInCache = taskCacheDataSource.getAllTasks()
        val newDate = DateUtil.getCurrentTimestamp()
        val taskToUpdate = allTaskInCache.random()

        taskCacheDataSource.updateTask(
            primaryKey = taskToUpdate.id,
            newTitle = "its is the best title",
            newBody = "this is not the best body",
            newIsDone = true,
            updated_at = newDate
        )

        //simulate some work with app here
        delay(2_000)
        //close and then relaunch app
        syncTasks.syncTasks()
        //simulate some work with app here
        delay(2_000)
        //close and then relaunch app
        syncTasks.syncTasks()

        //now check if cache update is the same (update at should not change
        // b/c task did not update after newDate)

        assertEquals(
            newDate,
            taskCacheDataSource.searchTaskById(taskToUpdate.id)?.updated_at
        )

    }

    @Test
    fun syncTask_insert_addRandomTasksToCacheThenSyncAndConfirmNetworkUpdated() = runBlocking {

        val randomTasks = TaskFactory.createListOfRandomTasks(50)

        taskCacheDataSource.insertTasks(randomTasks)
        delay(2_000)
        syncTasks.syncTasks()
        delay(2_000)
        syncTasks.syncTasks()
        delay(2_000)
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

        val randomTasks = TaskFactory.createListOfRandomTasks(50)

        taskNetworkDataSource.insertTasks(randomTasks)

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
                taskToUpdate.isDone,
                taskToUpdate.updated_at
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
            taskNetworkDataSource.updateTask(
                taskToUpdate,
                taskToUpdate.updated_at
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