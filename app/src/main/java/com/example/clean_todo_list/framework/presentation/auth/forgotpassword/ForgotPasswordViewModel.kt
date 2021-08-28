package com.example.clean_todo_list.framework.presentation.auth.forgotpassword

import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.framework.presentation.auth.forgotpassword.state.ForgotPasswordViewState
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import kotlinx.coroutines.FlowPreview

@FlowPreview
class ForgotPasswordViewModel : BaseViewModel<ForgotPasswordViewState>() {

    override fun handleNewData(data: ForgotPasswordViewState) {
        val outdated = getCurrentViewStateOrNew()
        val updatedVieState = ForgotPasswordViewState(
            email = data.email ?: outdated.email
        )
        setViewState(updatedVieState)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        TODO("Not yet implemented")
    }

    override fun initNewViewState(): ForgotPasswordViewState = ForgotPasswordViewState()
    // TODO: Implement the ViewModel
}