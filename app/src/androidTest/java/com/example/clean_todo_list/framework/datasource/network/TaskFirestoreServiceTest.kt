package com.example.clean_todo_list.framework.datasource.network

import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.runner.AndroidJUnitRunner
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.model.TaskFactory
import com.example.clean_todo_list.di.TestAppComponent
import com.example.clean_todo_list.framework.datasource.network.abstraction.TaskFirestoreService
import com.example.clean_todo_list.framework.datasource.network.implemetation.TaskFirestoreServiceImpl
import com.example.clean_todo_list.framework.presentation.TestBaseApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4ClassRunner::class)
class TaskFirestoreServiceTest {

    //system under test
    lateinit var taskFirestoreService: TaskFirestoreService

    val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext() as TestBaseApplication

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    init {
        (application.appComponent as TestAppComponent)
            .inject(this)
        signIn()
    }

    private fun signIn() = runBlocking {
        firebaseAuth.signInWithEmailAndPassword(
            TEST_EMAIL,
            TEST_PASSWORD
        ).await()
    }

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

    companion object {
        private const val TEST_EMAIL = "mahdi@test.com"
        private const val TEST_PASSWORD = "password"
    }
}