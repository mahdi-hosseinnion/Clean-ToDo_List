package com.example.clean_todo_list.framework.presentation.auth.forgotpassword.state

import com.example.clean_todo_list.business.domain.state.StateEvent

sealed class ForgotPasswordStateEvent : StateEvent {

    data class SendResetPasswordEmailEvent(
        val email: String
    ) : ForgotPasswordStateEvent() {
        override fun errorInfo(): String = "Unable to send reset password link"

        override fun eventName(): String = "Send Reset Password Email Event for email: $email"

        override fun shouldDisplayProgressBar(): Boolean = true

    }
}
