package com.example.clean_todo_list.business.data.network.auth.implementation

import com.example.clean_todo_list.business.data.network.auth.abstraction.AuthNetworkDataSource
import com.example.clean_todo_list.business.domain.state.DataState
import com.example.clean_todo_list.framework.datasource.network.auth.abstraction.AuthFirebaseService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthNetworkDataSourceImpl
@Inject
constructor(
    private val firebaseService: AuthFirebaseService
) : AuthNetworkDataSource {

    override suspend fun login(email: String, password: String): DataState<Nothing> =
        firebaseService.login(email, password)

    override suspend fun signup(email: String, password: String): DataState<Nothing> =
        firebaseService.signup(email, password)

    override suspend fun sendPasswordResetEmail(email: String): DataState<Nothing> =
        firebaseService.sendPasswordResetEmail(email)
}