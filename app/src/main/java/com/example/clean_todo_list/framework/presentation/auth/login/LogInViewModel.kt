package com.example.clean_todo_list.framework.presentation.auth.login

import com.example.clean_todo_list.business.domain.state.DataState
import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.business.interactors.auth.login.LoginUser
import com.example.clean_todo_list.framework.presentation.auth.login.state.LogInStateEvent
import com.example.clean_todo_list.framework.presentation.auth.login.state.LogInViewState
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@FlowPreview
class LogInViewModel(
    private val loginUser: LoginUser
) : BaseViewModel<LogInViewState>() {

    override fun handleNewData(data: LogInViewState) {
        val outdated = getCurrentViewStateOrNew()
        val updatedVieState = LogInViewState(
            email = data.email ?: outdated.email,
            password = data.password ?: outdated.password
        )
        setViewState(updatedVieState)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<LogInViewState>?> = when (
            stateEvent
        ) {
            is LogInStateEvent.LoginUserEvent -> {
                loginUser.execute(stateEvent.email, stateEvent.password, stateEvent)
            }
            else -> {
                emitInvalidStateEvent(stateEvent)
            }
        }
        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): LogInViewState = LogInViewState()

    fun login(
        email: String,
        password: String
    ) {
        setStateEvent(
            LogInStateEvent.LoginUserEvent(
                email,
                password
            )
        )
    }

    fun setEmail(email: String) {
        handleNewData(
            LogInViewState(
                email = email
            )
        )
    }

    fun setPassword(password: String) {
        handleNewData(
            LogInViewState(
                password = password
            )
        )
    }
}