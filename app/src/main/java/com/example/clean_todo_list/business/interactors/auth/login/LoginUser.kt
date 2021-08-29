package com.example.clean_todo_list.business.interactors.auth.login

import com.example.clean_todo_list.business.data.network.NetworkConstants.NETWORK_TIMEOUT
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.presentation.auth.login.state.LogInViewState
import com.example.clean_todo_list.util.printLogD
import com.example.clean_todo_list.util.printLogE
import com.google.firebase.auth.FirebaseAuth
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
    private val auth: FirebaseAuth
) {

    fun execute(
        email: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LogInViewState>> = flow {

        var result = failResult(stateEvent)
        try {
            withTimeout(LOGIN_USER_TIME_OUT) {

                auth.signInWithEmailAndPassword(
                    email,
                    password
                ).addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        result = successResult(stateEvent)
                        printLogD(TAG, "Login was successful")
                    } else {
                        result =
                            response.exception?.message?.let { failResult(stateEvent, it) }
                                ?: failResult(
                                    stateEvent
                                )
                        printLogE(TAG, "Login failed cause: ${response.exception}")
                    }
                }.await()

            }
        } catch (throwable: Throwable) {
            if (throwable is TimeoutCancellationException) {
                result = failResult(stateEvent, LOGIN_TIME_OUT_ERROR)
            }
        }
        emit(result)

    }

    private fun successResult(
        stateEvent: StateEvent
    ): DataState<LogInViewState> = DataState.data(
        response = Response(
            message = LOGIN_USER_SUCCESS,
            uiComponentType = UIComponentType.None,
            messageType = MessageType.Success
        ),
        data = null,
        stateEvent = stateEvent
    )

    private fun failResult(
        stateEvent: StateEvent,
        message: String = LOGIN_USER_FAILED
    ): DataState<LogInViewState> = DataState.error(
        response = Response(
            message = message,
            uiComponentType = UIComponentType.Toast,
            messageType = MessageType.Error
        ),
        stateEvent = stateEvent
    )

    companion object {
        private const val TAG = "LoginInUser"
        const val LOGIN_USER_SUCCESS = "Login user was successful"
        private const val LOGIN_USER_FAILED = "Unable to login user"
        private const val LOGIN_TIME_OUT_ERROR =
            "Login takes too long \n check your connection and try again"
        private const val LOGIN_USER_TIME_OUT = NETWORK_TIMEOUT
    }
}