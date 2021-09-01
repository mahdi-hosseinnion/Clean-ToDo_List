package com.example.clean_todo_list.framework.presentation.auth.forgotpassword

import com.example.clean_todo_list.business.domain.state.DataState
import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.business.interactors.auth.forgotpassword.SendResetPasswordEmail
import com.example.clean_todo_list.framework.presentation.auth.forgotpassword.state.ForgotPasswordStateEvent
import com.example.clean_todo_list.framework.presentation.auth.forgotpassword.state.ForgotPasswordViewState
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@FlowPreview
class ForgotPasswordViewModel(
    private val sendResetPasswordEmail: SendResetPasswordEmail
) : BaseViewModel<ForgotPasswordViewState>() {

    override fun handleNewData(data: ForgotPasswordViewState) {
        val outdated = getCurrentViewStateOrNew()
        val updatedVieState = ForgotPasswordViewState(
            email = data.email ?: outdated.email
        )
        setViewState(updatedVieState)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<ForgotPasswordViewState>?> = when (stateEvent) {
            is ForgotPasswordStateEvent.SendResetPasswordEmailEvent -> {
                sendResetPasswordEmail.execute(
                    email = stateEvent.email,
                    stateEvent = stateEvent
                )
            }
            else -> {
                emitInvalidStateEvent(stateEvent)
            }
        }
        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): ForgotPasswordViewState = ForgotPasswordViewState()

    fun sendResetPasswordEmail(email: String) {
        setStateEvent(
            ForgotPasswordStateEvent.SendResetPasswordEmailEvent(
                email
            )
        )
    }

    fun setEmail(email: String) {
        handleNewData(
            ForgotPasswordViewState(
                email = email
            )
        )
    }
}