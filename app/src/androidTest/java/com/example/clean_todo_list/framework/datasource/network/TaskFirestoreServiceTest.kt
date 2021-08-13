package com.example.clean_todo_list.framework.datasource.network

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.clean_todo_list.FirebaseBaseTest
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.di.TestAppComponent
import com.example.clean_todo_list.framework.datasource.network.abstraction.TaskFirestoreService
import com.example.clean_todo_list.framework.datasource.network.implemetation.TaskFirestoreServiceImpl
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4ClassRunner::class)
class TaskFirestoreServiceTest : FirebaseBaseTest() {

    //system under test
    lateinit var taskFirestoreService: TaskFirestoreService


    @Before
    fun init_systemUnderTest() {
        taskFirestoreService = TaskFirestoreServiceImpl(
            firestore
        )
    }

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

    override fun inject() {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }

}