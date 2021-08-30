package com.example.clean_todo_list.framework.datasource.network.auth.abstraction

import com.example.clean_todo_list.business.domain.state.DataState

interface AuthFirebaseService {

    suspend fun login(email: String, password: String): DataState<Nothing>

    suspend fun signup(email: String, password: String): DataState<Nothing>

    suspend fun sendPasswordResetEmail(email: String): DataState<Nothing>

}