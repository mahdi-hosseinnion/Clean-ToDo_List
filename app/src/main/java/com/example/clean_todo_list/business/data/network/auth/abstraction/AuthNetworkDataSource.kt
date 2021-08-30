package com.example.clean_todo_list.business.data.network.auth.abstraction

import com.example.clean_todo_list.business.domain.state.DataState

interface AuthNetworkDataSource {

    suspend fun login(email: String, password: String): DataState<Nothing>

    suspend fun signup(email: String, password: String): DataState<Nothing>

    suspend fun sendPasswordResetEmail(email: String): DataState<Nothing>
}