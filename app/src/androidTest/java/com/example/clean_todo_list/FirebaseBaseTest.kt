package com.example.clean_todo_list


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

abstract class FirebaseBaseTest : BaseTest() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    init {
        signIn()
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