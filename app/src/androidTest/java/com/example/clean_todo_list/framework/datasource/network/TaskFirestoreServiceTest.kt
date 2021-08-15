package com.example.clean_todo_list.framework.datasource.network

import android.text.format.DateUtils
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.clean_todo_list.FirebaseBaseTest
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.business.domain.util.DateUtil
import com.example.clean_todo_list.di.TestAppComponent
import com.example.clean_todo_list.framework.datasource.network.abstraction.TaskFirestoreService
import com.example.clean_todo_list.framework.datasource.network.implemetation.TaskFirestoreServiceImpl
import com.example.clean_todo_list.util.printLogD
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.*

/**
LEGEND:
1. CBS = "Confirm by searching"
Test cases:
1. insert a single task, CBS
2. update a random task, CBS
3. update a isDone of a task, CBS
4. insert a list of tasks, CBS
5. delete a single task, CBS
6. insert a deleted task into "deletes" node, CBS
7. insert a list of deleted tasks into "deletes" node, CBS
8. delete a 'deleted tasks' (task from "deletes" node). CBS
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class TaskFirestoreServiceTest : FirebaseBaseTest() {

    //system under test
    lateinit var taskFirestoreService: TaskFirestoreService

    init {
        insertTestData()
    }

    @Before
    fun init_systemUnderTest() {
        taskFirestoreService = TaskFirestoreServiceImpl(
            firestore
        )
    }

    //    1. insert a single task, CBS
    @Test
    fun insertOneTask_CBS() = runBlocking {
        val task = TaskFactory.createRandomTask()
        //make sure task does not exist in network
        assertNull(
            taskFirestoreService.searchTask(task)
        )
        //insert that task
        taskFirestoreService.insertOrUpdateTask(task)
        //check task actually inserted
        val insertedTask = taskFirestoreService.searchTask(task)
        //check like this b/c updated_at will update in insertOrUpdateTask function
        assertEquals(task.id, insertedTask?.id)
        assertEquals(task.title, insertedTask?.title)
        assertEquals(task.body, insertedTask?.body)
        assertEquals(task.isDone, insertedTask?.isDone)
        assertEquals(task.created_at, insertedTask?.created_at)
    }

    //    2. update a random task, CBS
    @Test
    fun updateTask_CBS() = runBlocking {
        val allTaskInNetwork = taskFirestoreService.getAllTasks()

        //select random task
        val randomTask = allTaskInNetwork.random()

        //create completely different task but with same id

        val taskToUpdate = randomTask.copy(
            title = "A SUPER FUN TITLE FOR UPDATE",
            body = "A VERY FUN BODY FOR UPDATE THIS TASK HAHA"
        )
        //update in network
        taskFirestoreService.insertOrUpdateTask(taskToUpdate)

        //confirm task was updated
        val updatedTask = taskFirestoreService.searchTask(taskToUpdate)

        assertEquals(taskToUpdate.id, updatedTask?.id)
        assertEquals(taskToUpdate.title, updatedTask?.title)
        assertEquals(taskToUpdate.body, updatedTask?.body)
        assertEquals(taskToUpdate.isDone, updatedTask?.isDone)
        assertEquals(taskToUpdate.created_at, updatedTask?.created_at)

        assertNotEquals(taskToUpdate.updated_at, updatedTask?.updated_at)
    }

    //    3. update a isDone of a task, CBS
    @Test
    fun updateIsDone_CBS() = runBlocking {
        val allTaskInNetwork = taskFirestoreService.getAllTasks()

        //select random task
        val randomTask = allTaskInNetwork.random()
        //change isDone
        val taskToUpdate = randomTask.copy(
            isDone = !randomTask.isDone,
        )
        printLogD("updateTask_CBS", "taskToUpdate = $taskToUpdate")
        //update in network (reverse isDoneState)
        taskFirestoreService.updateIsDone(taskToUpdate.id, taskToUpdate.isDone)

        //confirm task was updated
        val updatedTask = taskFirestoreService.searchTask(taskToUpdate)

        assertEquals(taskToUpdate.id, updatedTask?.id)
        assertEquals(taskToUpdate.title, updatedTask?.title)
        assertEquals(taskToUpdate.body, updatedTask?.body)
        assertEquals(taskToUpdate.created_at, updatedTask?.created_at)

        assertNotEquals(taskToUpdate.isDone, updatedTask?.isDone)
        assertNotEquals(taskToUpdate.updated_at, updatedTask?.updated_at)
    }

    //    4. insert a list of tasks, CBS
    @Test
    fun insertListOfTasks_CBS() = runBlocking {

        val tasksToInsert = TaskFactory.createListOfRandomTasks(10)

        //insert them
        taskFirestoreService.insertOrUpdateTasks(tasksToInsert)

        //confirm tasks inserted
        val allTasksInNetworkAfterInsert = taskFirestoreService.getAllTasks()

        assertTrue {
            allTasksInNetworkAfterInsert.containsAll(
                tasksToInsert
            )
        }
    }

    //    5. delete a single task, CBS
    @Test
    fun deleteRandomTask_CBS() = runBlocking {
        val allTasksInNetwork = taskFirestoreService.getAllTasks()

        val taskToDelete = allTasksInNetwork.random()
        //confirm task actually exist in network
        assertNotNull(
            taskFirestoreService.searchTask(taskToDelete)
        )
        //delete that task
        taskFirestoreService.deleteTask(taskToDelete.id)
        //confirm task been deleted
        assertNull(
            taskFirestoreService.searchTask(taskToDelete)
        )
    }

    //    6. insert a deleted task into "deletes" node, CBS
    @Test
    fun insertTaskIntoDeletedNode_CBS() = runBlocking {

        val taskToInsertIntoDeleteNode = TaskFactory.createRandomTask()

        //confirm this task does not exist in any node
        assertNull(
            taskFirestoreService.searchTask(taskToInsertIntoDeleteNode)
        )
        assertFalse {
            taskFirestoreService.getDeletedTasks().contains(taskToInsertIntoDeleteNode)
        }
        //insert task into deleted node
        taskFirestoreService.insertDeletedTask(taskToInsertIntoDeleteNode)
        //confirm inserted into 'deleted' node
        assertTrue {
            taskFirestoreService.getDeletedTasks()
                .contains(taskToInsertIntoDeleteNode)
        }
    }

    //    7. insert a list of deleted tasks into "deletes" node, CBS
    @Test
    fun insertListOfTaskIntoDeleteNode_CBS() = runBlocking {
        val tasksToInsert = TaskFactory.createListOfRandomTasks(10)

        //insert them
        taskFirestoreService.insertDeletedTasks(tasksToInsert)

        //confirm tasks inserted
        val allTasksInNetworkAfterInsert = taskFirestoreService.getDeletedTasks()

        assertTrue {
            allTasksInNetworkAfterInsert.containsAll(
                tasksToInsert
            )
        }
    }

    //    8. delete a 'deleted tasks' (task from "deletes" node). CBS
    @Test
    fun deleteDeletedTask_CBS() = runBlocking {
        //insert 10 random task into deleted node
        val tasksToInsert = TaskFactory.createListOfRandomTasks(10)

        //insert them
        taskFirestoreService.insertDeletedTasks(tasksToInsert)
        //confirm tasks inserted
        val allTasksInNetworkAfterInsert = taskFirestoreService.getDeletedTasks()

        assertTrue {
            allTasksInNetworkAfterInsert.containsAll(
                tasksToInsert
            )
        }
        //now delete one of them
        val taskToDelete = taskFirestoreService.getDeletedTasks().random()

        //delete from 'deleted node'
        taskFirestoreService.deleteDeletedTask(taskToDelete)

        //confirm task deleted
        val allTasksInNetworkAfterDelete = taskFirestoreService.getDeletedTasks()

        assertFalse {
            allTasksInNetworkAfterDelete.contains(
                taskToDelete
            )
        }
    }

    override fun inject() {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }

}