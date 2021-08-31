package com.example.clean_todo_list.framework.presentation.auth.login.state

import com.example.clean_todo_list.business.domain.state.StateEvent

sealed class LogInStateEvent : StateEvent {
    data class LoginUserEvent(

        val email: String,
        val password: String
    ) : LogInStateEvent() {
        override fun errorInfo(): String = "Unable to login"

        override fun eventName(): String = "Login user ${this.hashCode()}"

        override fun shouldDisplayProgressBar(): Boolean = true
    }
}
