package com.example.clean_todo_list.framework.datasource.network.auth.implementation

import com.example.clean_todo_list.business.domain.state.DataState
import com.example.clean_todo_list.business.domain.state.MessageType
import com.example.clean_todo_list.business.domain.state.Response
import com.example.clean_todo_list.business.domain.state.UIComponentType
import com.example.clean_todo_list.framework.datasource.network.auth.abstraction.AuthFirebaseService
import com.example.clean_todo_list.util.printLogD
import com.example.clean_todo_list.util.printLogE
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthFirebaseServiceImpl(
    private val auth: FirebaseAuth
) : AuthFirebaseService {

    override suspend fun login(email: String, password: String): DataState<Nothing> {
        var result = failResult(LOGIN_UNKNOWN_ERROR)

        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { response ->
            if (response.isSuccessful) {
                result = successResult(LOGIN_SUCCESS)
                printLogD(TAG, LOGIN_SUCCESS)

            } else {
                //fail
                //TODO check for localized message
                result = failResult(LOGIN_ERROR + response.exception?.localizedMessage)
                printLogE(TAG, response.exception.toString())
            }
        }.await()

        return result
    }

    override suspend fun signup(email: String, password: String): DataState<Nothing> {
        var result = failResult(SIGNUP_UNKNOWN_ERROR)

        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { response ->
            if (response.isSuccessful) {
                result = successResult(SIGNUP_SUCCESS)
                printLogD(TAG, SIGNUP_SUCCESS)
            } else {
                //fail
                //TODO check for localized message
                result = failResult(SIGNUP_ERROR + response.exception?.localizedMessage)
                printLogE(TAG, response.exception.toString())
            }
        }.await()

        return result
    }

    override suspend fun sendPasswordResetEmail(email: String): DataState<Nothing> {
        var result = failResult(SEND_RESET_UNKNOWN_ERROR)

        auth.sendPasswordResetEmail(
            email
        ).addOnCompleteListener { response ->
            if (response.isSuccessful) {
                result = successResult(SEND_RESET_SUCCESS)
                printLogD(TAG, SEND_RESET_SUCCESS)
            } else {
                //fail
                //TODO check for localized message
                result = failResult(SEND_RESET_ERROR + response.exception?.localizedMessage)
                printLogE(TAG, response.exception.toString())
            }
        }.await()

        return result
    }

    private fun successResult(
        message: String
    ): DataState<Nothing> = DataState.data(
        response = Response(
            message = message,
            uiComponentType = UIComponentType.Toast,
            messageType = MessageType.Success
        ),
        data = null,
        stateEvent = null
    )

    private fun failResult(
        message: String
    ): DataState<Nothing> = DataState.error(
        response = Response(
            message = message,
            uiComponentType = UIComponentType.Dialog,
            messageType = MessageType.Error
        ),
        stateEvent = null
    )

    companion object {
        private const val TAG = "AuthFirestoreServiceImp"

        //success messages
        private const val LOGIN_SUCCESS = "The login was successful"
        private const val SIGNUP_SUCCESS = "The sign up was successful"
        private const val SEND_RESET_SUCCESS = "Password reset email sent"

        //errro messages
        private const val LOGIN_ERROR = "Unable to login\nreason: "
        private const val SIGNUP_ERROR = "Unable to sing up \nreason: "
        private const val SEND_RESET_ERROR =
            "Unable to send password reset email!\nreason: "

        //unknown errors
        private const val LOGIN_UNKNOWN_ERROR = "Unable to login\nreason: unknown"
        private const val SIGNUP_UNKNOWN_ERROR = "Unable to sing up \nreason: unknown"
        private const val SEND_RESET_UNKNOWN_ERROR =
            "Unable to send password reset email!\nreason: unknown"
    }

    //TODO ADD PERSAION LANGUAGE
    //doc: https://firebase.google.com/docs/auth/android/manage-users#send_a_password_reset_email
//    auth.setLanguageCode("fr")
//    // To apply the default app language instead of explicitly setting it.
//// auth.useAppLanguage()
}