package com.example.clean_todo_list.business.data.network.auth.abstraction

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface AuthNetworkDataSource {

    suspend fun login(email: String, password: String): Task<AuthResult>?

    suspend fun signup(email: String, password: String): Task<AuthResult>?

    suspend fun sendPasswordResetEmail(email: String): Task<Void>?
}