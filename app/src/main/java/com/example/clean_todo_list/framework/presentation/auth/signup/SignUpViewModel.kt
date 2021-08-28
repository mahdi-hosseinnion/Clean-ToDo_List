package com.example.clean_todo_list.framework.presentation.auth.signup

import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.framework.presentation.auth.signup.state.SignUpViewState
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel

class SignUpViewModel : BaseViewModel<SignUpViewState>() {

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
        TODO("Not yet implemented")
    }

    override fun initNewViewState(): SignUpViewState = SignUpViewState()
    // TODO: Implement the ViewModel
}