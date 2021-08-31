package com.example.clean_todo_list.framework.presentation.auth.signup

import com.example.clean_todo_list.business.domain.state.DataState
import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.business.interactors.auth.signup.SignUpUser
import com.example.clean_todo_list.framework.presentation.auth.signup.state.SignUpStateEvent
import com.example.clean_todo_list.framework.presentation.auth.signup.state.SignUpViewState
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@FlowPreview
class SignUpViewModel(
    private val signUpUser: SignUpUser
) : BaseViewModel<SignUpViewState>() {

    override fun handleNewData(data: SignUpViewState) {
        val outdated = getCurrentViewStateOrNew()
        val updatedVieState = SignUpViewState(
            email = data.email ?: outdated.email,
            password = data.password ?: outdated.password,
            repeatPassword = data.repeatPassword ?: outdated.repeatPassword
        )
        setViewState(updatedVieState)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<SignUpViewState>?> = when (stateEvent) {
            is SignUpStateEvent.SignUpUserEvent -> {
                signUpUser.execute(
                    email = stateEvent.email,
                    password = stateEvent.password,
                    stateEvent = stateEvent
                )
            }
            else -> {
                emitInvalidStateEvent(stateEvent)
            }

        }
        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): SignUpViewState = SignUpViewState()

    fun signUp(
        email: String,
        password: String
    ) {
        setStateEvent(
            SignUpStateEvent.SignUpUserEvent(
                email,
                password
            )
        )
    }
}