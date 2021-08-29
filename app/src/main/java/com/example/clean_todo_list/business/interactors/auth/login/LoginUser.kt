package com.example.clean_todo_list.business.interactors.auth.login

import android.service.autofill.Dataset
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.framework.presentation.auth.login.state.LogInViewState
import com.example.clean_todo_list.util.printLogD
import com.example.clean_todo_list.util.printLogE
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class LoginUser(
    private val auth: FirebaseAuth
) {

    fun execute(
        email: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LogInViewState>> = flow {

        var result = failResult(stateEvent)

        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { response ->
            if (response.isSuccessful) {
                result = successResult(stateEvent)
                printLogD(TAG, "Login was successful")
            } else {
                result =
                    response.exception?.message?.let { failResult(stateEvent, it) } ?: failResult(stateEvent)
                printLogE(TAG, "Login failed cause: ${response.exception}")
            }
        }.await()

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
            message = LOGIN_USER_SUCCESS,
            uiComponentType = UIComponentType.None,
            messageType = MessageType.Success
        ),
        stateEvent = stateEvent
    )

    companion object {
        private const val TAG = "LoginInUser"
        private const val LOGIN_USER_SUCCESS = "Login user was successful"
        private const val LOGIN_USER_FAILED = "Unable to login user"
    }
}