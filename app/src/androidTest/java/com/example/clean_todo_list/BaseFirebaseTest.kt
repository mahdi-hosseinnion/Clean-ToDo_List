package com.example.clean_todo_list


import com.example.clean_todo_list.framework.datasource.data.TaskDataFactory
import com.example.clean_todo_list.framework.datasource.network.implemetation.TaskFirestoreServiceImpl.Companion.TASKS_COLLECTION
import com.example.clean_todo_list.framework.datasource.network.implemetation.TaskFirestoreServiceImpl.Companion.USER_ID
import com.example.clean_todo_list.framework.datasource.network.mappers.NetworkMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

abstract class BaseFirebaseTest : BaseTest() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var taskDataFactory: TaskDataFactory

    init {
        signIn()
    }

    fun insertTestData() {
        val data =
            NetworkMapper.mapDomainModelListToEntityList(
                taskDataFactory.produceListOfTasks()
            )
        for (taskEntity in data) {
            firestore
                .collection(TASKS_COLLECTION)
                .document(USER_ID)
                .collection(TASKS_COLLECTION)
                .document(taskEntity.id)
                .set(taskEntity)
        }
    }

    private fun signIn() = runBlocking {
        firebaseAuth.signInWithEmailAndPassword(
            TEST_EMAIL,
            TEST_PASSWORD
        ).await()
    }


    companion object {
        private const val TEST_EMAIL = "mahdi@test.com"
        private const val TEST_PASSWORD = "password"
    }
}