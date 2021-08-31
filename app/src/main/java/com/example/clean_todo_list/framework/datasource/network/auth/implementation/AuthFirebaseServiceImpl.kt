package com.example.clean_todo_list.framework.datasource.network.auth.implementation

import com.example.clean_todo_list.framework.datasource.network.auth.abstraction.AuthFirebaseService
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthFirebaseServiceImpl(
    private val auth: FirebaseAuth
) : AuthFirebaseService {

    override suspend fun login(email: String, password: String): Task<AuthResult>? {
//        return auth.signInWithEmailAndPassword(email, password).asDeferred().asTask()
        var result: Task<AuthResult>? = null
        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener {
            result = it
        }.await()
        return result
    }

    override suspend fun signup(email: String, password: String): Task<AuthResult>? {
        var result: Task<AuthResult>? = null
        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener {
            result = it
        }.await()
        return result
    }

    override suspend fun sendPasswordResetEmail(email: String): Task<Void>? {
        var result: Task<Void>? = null
        auth.sendPasswordResetEmail(
            email
        ).addOnCompleteListener {
            result = it
        }.await()
        return result
    }

    companion object {
        private const val TAG = "AuthFirestoreServiceImp"

        //errro messages
        private const val LOGIN_ERROR = "Unable to login\nreason: "
        private const val SIGNUP_ERROR = "Unable to sing up \nreason: "
        private const val SEND_RESET_ERROR =
            "Unable to send password reset email!\nreason: "

        //unknown errors
        private const val LOGIN_UNKNOWN_ERROR = "Unable to login\nreason: unknown"
        private const val SIGNUP_UNKNOWN_ERROR = "Unable to sing up \nreason: unknown"
        private const val SEND_RESET_UNKNOWN_ERROR =
            "Unable to send password reset email!\nreason: unknown"
    }

//TODO ADD PERSAION LANGUAGE
//doc: https://firebase.google.com/docs/auth/android/manage-users#send_a_password_reset_email
//    auth.setLanguageCode("fr")
//    // To apply the default app language instead of explicitly setting it.
//// auth.useAppLanguage()
}