package com.example.clean_todo_list.business.interactors.auth.signup

import com.example.clean_todo_list.business.data.network.ApiResponseHandler
import com.example.clean_todo_list.business.data.network.auth.abstraction.AuthNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeAuthCall
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.presentation.auth.signup.state.SignUpViewState
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpUser
@Inject
constructor(
    private val authNetworkDataSource: AuthNetworkDataSource
) {

    fun execute(
        email: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<SignUpViewState>?> = flow {

        val networkResult = safeAuthCall(Dispatchers.IO) {
            authNetworkDataSource.signup(email, password)
        }
        val response = object : ApiResponseHandler<SignUpViewState, AuthResult>(
            response = networkResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: AuthResult): DataState<SignUpViewState>? {
                return DataState.data(
                    response = Response(
                        message = SIGNUP_SUCCESS,
                        uiComponentType = UIComponentType.Toast,
                        messageType = MessageType.Success
                    ),
                    data = null,
                    stateEvent = stateEvent
                )
            }
        }.getResult()

        emit(response)
    }

    companion object {
        private const val SIGNUP_SUCCESS = "The sign up was successful"

    }
}