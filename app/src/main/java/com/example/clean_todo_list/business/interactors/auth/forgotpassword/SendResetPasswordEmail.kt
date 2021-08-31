package com.example.clean_todo_list.business.interactors.auth.forgotpassword

import com.example.clean_todo_list.business.data.network.ApiResponseHandler
import com.example.clean_todo_list.business.data.network.auth.abstraction.AuthNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeAuthCall
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.presentation.auth.forgotpassword.state.ForgotPasswordViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SendResetPasswordEmail
@Inject
constructor(
    private val authNetworkDataSource: AuthNetworkDataSource
) {

    fun execute(
        email: String,
        stateEvent: StateEvent
    ): Flow<DataState<ForgotPasswordViewState>?> = flow {

        val networkResult = safeAuthCall(IO) {
            authNetworkDataSource.sendPasswordResetEmail(email)
        }
        val response = object : ApiResponseHandler<ForgotPasswordViewState, Void>(
            response = networkResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Void): DataState<ForgotPasswordViewState>? {
                return DataState.data(
                    response = Response(
                        message = SEND_RESET_SUCCESS,
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
        private const val SEND_RESET_SUCCESS = "Password reset email sent"
    }
}
















