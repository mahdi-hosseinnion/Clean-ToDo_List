package com.example.clean_todo_list.business.data.network.auth.implementation

import com.example.clean_todo_list.business.data.network.auth.abstraction.AuthNetworkDataSource
import com.example.clean_todo_list.business.domain.state.DataState
import com.example.clean_todo_list.framework.datasource.network.auth.abstraction.AuthFirebaseService
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthNetworkDataSourceImpl
@Inject
constructor(
    private val firebaseService: AuthFirebaseService
) : AuthNetworkDataSource {

    override suspend fun login(email: String, password: String): Task<AuthResult>? =
        firebaseService.login(email, password)

    override suspend fun signup(email: String, password: String): Task<AuthResult>? =
        firebaseService.signup(email, password)

    override suspend fun sendPasswordResetEmail(email: String): Task<Void>? =
        firebaseService.sendPasswordResetEmail(email)
}