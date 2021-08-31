package com.example.clean_todo_list.framework.presentation.auth.signup.state

import com.example.clean_todo_list.business.domain.state.StateEvent

sealed class SignUpStateEvent : StateEvent {

    data class SignUpUserEvent(
        val email: String,
        val password: String
    ) : SignUpStateEvent() {

        override fun errorInfo(): String = "Unable to sign up"

        override fun eventName(): String = "Sign up user event hash: ${this.hashCode()}"

        override fun shouldDisplayProgressBar(): Boolean = true
    }
}
