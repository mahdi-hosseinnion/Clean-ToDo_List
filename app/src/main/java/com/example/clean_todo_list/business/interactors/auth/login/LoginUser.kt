package com.example.clean_todo_list.business.interactors.auth.login

import com.example.clean_todo_list.business.data.network.ApiResponseHandler
import com.example.clean_todo_list.business.data.network.NetworkConstants.NETWORK_TIMEOUT
import com.example.clean_todo_list.business.data.network.auth.abstraction.AuthNetworkDataSource
import com.example.clean_todo_list.business.data.util.safeAuthCall
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.presentation.auth.login.state.LogInViewState
import com.example.clean_todo_list.util.printLogD
import com.example.clean_todo_list.util.printLogE
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.lang.Exception
import javax.inject.Inject

//TODO AUTH SCOPOE
class LoginUser
@Inject
constructor(
    private val authNetworkDataSource: AuthNetworkDataSource
) {

    fun execute(
        email: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LogInViewState>?> = flow {

        val networkResult = safeAuthCall(IO) {
            authNetworkDataSource.login(
                email,
                password
            )
        }
        val response = object : ApiResponseHandler<LogInViewState, AuthResult>(
            response = networkResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: AuthResult): DataState<LogInViewState>? {
                return DataState.data(
                    response = Response(
                        message = LOGIN_USER_SUCCESS,
                        uiComponentType = UIComponentType.Toast,
                        messageType = MessageType.Success
                    ),
                    data = LogInViewState(),
                    stateEvent = stateEvent
                )
            }
        }.getResult()

        emit(response)

    }

    companion object {
        private const val TAG = "LoginInUser"
        const val LOGIN_USER_SUCCESS = "The login was successful"
    }
}