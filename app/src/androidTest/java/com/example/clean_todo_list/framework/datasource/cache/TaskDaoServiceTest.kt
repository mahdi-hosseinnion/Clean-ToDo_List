package com.example.clean_todo_list.framework.datasource.cache

import com.example.clean_todo_list.BaseCacheTest
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.di.TestAppComponent
import com.example.clean_todo_list.framework.datasource.cache.abstraction.TaskDaoService
import com.example.clean_todo_list.framework.datasource.cache.implementation.TaskDaoServiceImpl
import com.example.clean_todo_list.util.printLogD
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.test.*

/**
LEGEND:
1. CBS = "Confirm by searching"

Test cases:
1. confirm database task empty to start (should be test data inserted from CacheTest.kt)
2. insert a new task, CBS
3. insert a list of tasks, CBS
4. insert 1000 new tasks, confirm filtered search query works correctly
5. insert 1000 new tasks, confirm db size increased
6. delete new task, confirm deleted
7. delete list of tasks, CBS
8. update a task, confirm updated
9. update isDone of task, confirm updated
9. search tasks, order by date (ASC), confirm order
10. search tasks, order by date (DESC), confirm order
11. search tasks, order by title (ASC), confirm order
12. search tasks, order by title (DESC), confirm order

 */
class TaskDaoServiceTest : BaseCacheTest() {

    //system under test 
    private val taskDaoService: TaskDaoService

    init {
        insertTestData()
        taskDaoService = TaskDaoServiceImpl(taskDao)
    }

    override fun inject() {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }


    /** confirm database is not empty (there should be test data inserted via insertTestData method)
     *  this test should run first
     */

    @Test
    fun a_ConfirmDataBaseIsNotEmpty() = runBlocking {
        val allTasksInCache = taskDaoService.getAllTasks()
        assertTrue {
            allTasksInCache.size > 5
        }
    }

    //    2. insert a new task, CBS
    @Test
    fun insertTask_CBS() = runBlocking {

        val taskToInsert = TaskFactory.createRandomTask()
        //make sure inserted
        assertNull(taskDaoService.searchTaskById(taskToInsert.id))
        //insert task
        taskDaoService.insertTask(taskToInsert)
        //confirm actually inserted
        assertEquals(
            taskToInsert,
            taskDaoService.searchTaskById(taskToInsert.id)
        )
    }

    //    3. insert a list of tasks, CBS
    @Test
    fun insertListOfTasks_CBS() = runBlocking {

        val tasksToInsert = TaskFactory.createListOfRandomTasks(10)
        //make sure inserted
        for (task in tasksToInsert) {
            assertNull(taskDaoService.searchTaskById(task.id))
        }
        //insert task
        taskDaoService.insertTasks(tasksToInsert)

        //confirm actually inserted
        val allTaskInCAcheAfterInsertion = taskDaoService.getAllTasks()

        assertTrue {
            allTaskInCAcheAfterInsertion.containsAll(
                tasksToInsert
            )
        }
    }

    //    4. insert 1000 new tasks, confirm filtered search query works correctly
    @Test
    fun searchTaskTest() = runBlocking {
        val tasksToInsert = TaskFactory.createListOfRandomTasks(1_000)
        taskDaoService.insertTasks(tasksToInsert)

        repeat(500) {
            val randomTask = tasksToInsert.random()
            val query = takeRandomPartOfTaskTitleOrBody(randomTask)

            val searchResults = taskDaoService.searchTasksOrderByTitleASC(query, 100)
            printLogD(
                "search for | $query | returns ${searchResults.size} results ",
                "searchTaskTest:"
            )
            for (searchResponse in searchResults)
                assertContains(
                    (searchResponse.title + " " + searchResponse.body).lowercase(Locale.getDefault()),
                    (query).lowercase(Locale.getDefault())
                )
        }
    }

    private fun takeRandomPartOfTaskTitleOrBody(task: Task): String =
        if (Random.nextBoolean() && task.body.isNotEmpty()) {
            //body
            val randomStart: Int = Random.nextInt(task.body.length)
            task.body.substring(randomStart)
        } else {
            //title
            val randomStart: Int = Random.nextInt(task.title.length)
            task.title.substring(randomStart)
        }
//    5. insert 1000 new tasks, confirm db size increased

    @Test
    fun insert1000Tas_confirmNumOfTaskIncreased() = runBlocking {

        val dbSizeBeforeInsertion = taskDaoService.getNumOfTasks()
        printLogD(
            "dbSizeBeforeInsertion: $dbSizeBeforeInsertion",
            "insert1000Task_confirmDbSizeIncreased"
        )
        val countOfInsertion = 1_000
        //insert tasks
        taskDaoService.insertTasks(TaskFactory.createListOfRandomTasks(countOfInsertion))

        //Assertion: confirm db size increased
        assertEquals(
            dbSizeBeforeInsertion.plus(countOfInsertion),
            taskDaoService.getNumOfTasks()
        )

    }

    //    6. delete new task, confirm deleted
    @Test
    fun deleteRandomTask_confirmDeleted() = runBlocking {
        //Arrange
        val allTasksInCacheBeforeDelete = taskDaoService.getAllTasks()
        val taskToDelete = allTasksInCacheBeforeDelete.random()

        //Act
        taskDaoService.deleteTask(taskToDelete.id)

        //Assert
        assertNull(
            taskDaoService.searchTaskById(taskToDelete.id)
        )

    }
//    7. delete list of tasks, CBS

    @Test
    fun deleteBunchOfTasks_confirmDeleted() = runBlocking {
        //Arrange
        val allTasksInCacheBeforeDelete = taskDaoService.getAllTasks()
        //select 5 random task from cache to delete
        val tasksToDelete = ArrayList<Task>()
        while (tasksToDelete.size >= 5) {

            val randomTask = allTasksInCacheBeforeDelete.random()
            if (!tasksToDelete.contains(randomTask)) {
                tasksToDelete.add(randomTask)
            }
        }
        printLogD(
            "deleteBunchOfTasks_confirmDeleted",
            "${tasksToDelete.size} task are going to delete $tasksToDelete"
        )

        //Act
        taskDaoService.deleteTasks(tasksToDelete)

        //Assert: confirm all deleted

        for (task in tasksToDelete) {
            assertNull(
                taskDaoService.searchTaskById(task.id)
            )
        }

    }


    //    8. update a task, confirm updated
    @Test
    fun updateRandomTask_confirmUpdated() = runBlocking {
        //Arrange
        val allTasksInCacheBeforeUpdate = taskDaoService.getAllTasks()
        val randomTask = allTasksInCacheBeforeUpdate.random()

        //Act
        val newTitle = "HEY YOU THIS TITLE IS DOPE"
        val newBody = "VERY DOPE body"
        val newIsDone = Random.nextBoolean()
        //delay to make sure updated time different
        delay(2_000)
        taskDaoService.updateTask(
            primaryKey = randomTask.id,
            newTitle = newTitle,
            newBody = newBody,
            newIsDone = newIsDone
        )

        //Assert: confirm updated
        val updatedTask = taskDaoService.searchTaskById(randomTask.id)!!

        assertEquals(randomTask.id, updatedTask.id)
        assertEquals(randomTask.created_at, updatedTask.created_at)

        assertEquals(newTitle, updatedTask.title)
        assertEquals(newBody, updatedTask.body)
        assertEquals(newIsDone, updatedTask.isDone)

        assertNotEquals(randomTask.updated_at, updatedTask.updated_at)
    }

    //    9. update isDone of task, confirm updated
    @Test
    fun updateIsDoneOfTask_confirmUpdated() = runBlocking {
        //Arrange
        val allTasksInCacheBeforeUpdate = taskDaoService.getAllTasks()
        val randomTask = allTasksInCacheBeforeUpdate.random()

        //Act
        //delay to make sure updated time different
        delay(2_000)
        val newIsDone = !randomTask.isDone
        taskDaoService.updateIsDone(
            primaryKey = randomTask.id,
            isDone = newIsDone
        )

        //Assert: confirm updated
        val updatedTask = taskDaoService.searchTaskById(randomTask.id)!!

        assertEquals(randomTask.id, updatedTask.id)
        assertEquals(randomTask.created_at, updatedTask.created_at)
        assertEquals(randomTask.title, updatedTask.title)
        assertEquals(randomTask.body, updatedTask.body)

        assertEquals(newIsDone, updatedTask.isDone)

        assertNotEquals(randomTask.isDone, updatedTask.isDone)
        assertNotEquals(randomTask.updated_at, updatedTask.updated_at)
    }

    //    9. search tasks, order by date (ASC), confirm order
    @Test
    fun searchTasksOrderByDateAsc_confirmOrder() = runBlocking {
        val tasksList = taskDaoService.searchTasksOrderByDateASC("", 1, 100)

        var previousTaskUpdatedAt = tasksList[0].updated_at
        for (i in 1 until tasksList.size) {
            val currentTaskUpdatedAt = tasksList[i].updated_at

            assertTrue {
                currentTaskUpdatedAt >= previousTaskUpdatedAt
            }

            previousTaskUpdatedAt = currentTaskUpdatedAt
        }
    }

    //    10. search tasks, order by date (DESC), confirm order
    @Test
    fun searchTasksOrderByDateDesc_confirmOrder() = runBlocking {
        //act
        val tasksList = taskDaoService.searchTasksOrderByDateDESC("", 1, 100)

        var previousTaskUpdatedAt = tasksList[0].updated_at
        for (i in 1 until tasksList.size) {
            val currentTaskUpdatedAt = tasksList[i].updated_at

            assertTrue {
                currentTaskUpdatedAt <= previousTaskUpdatedAt
            }

            previousTaskUpdatedAt = currentTaskUpdatedAt
        }
    }

    //    11. search tasks, order by title (ASC), confirm order
    @Test
    fun searchTasksOrderByTitleAsc_confirmOrder() = runBlocking {
        val tasksList = taskDaoService.searchTasksOrderByTitleASC("", 1, 100)

        var previousTaskTitle = tasksList[0].title
        for (i in 1 until tasksList.size) {
            val currentTaskTitle = tasksList[i].title

            assertTrue {
                listOf(previousTaskTitle, currentTaskTitle)
                    .asSequence()
                    .zipWithNext { a, b ->
                        a <= b
                    }.all { it }
            }

            previousTaskTitle = currentTaskTitle
        }
    }

    //    12. search tasks, order by title (DESC), confirm order
    @Test
    fun searchTasksOrderByTitleDesc_confirmOrder() = runBlocking {
        val tasksList = taskDaoService.searchTasksOrderByTitleDESC("", 1, 100)

        var previousTaskTitle = tasksList[0].title
        for (i in 1 until tasksList.size) {
            val currentTaskTitle = tasksList[i].title

            assertTrue {
                listOf(previousTaskTitle, currentTaskTitle)
                    .asSequence()
                    .zipWithNext { a, b ->
                        a >= b
                    }.all { it }
            }

            previousTaskTitle = currentTaskTitle
        }
    }
}