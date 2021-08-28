package com.example.clean_todo_list.framework.presentation.auth.login

import com.example.clean_todo_list.business.domain.state.StateEvent
import com.example.clean_todo_list.framework.presentation.auth.login.state.LogInViewState
import com.example.clean_todo_list.framework.presentation.common.BaseViewModel
import kotlinx.coroutines.FlowPreview

@FlowPreview
class LogInViewModel : BaseViewModel<LogInViewState>() {

    override fun handleNewData(data: LogInViewState) {
        val outdated = getCurrentViewStateOrNew()
        val updatedVieState = LogInViewState(
            email = data.email ?: outdated.email,
            password = data.password ?: outdated.password
        )
        setViewState(updatedVieState)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        TODO("Not yet implemented")
    }

    override fun initNewViewState(): LogInViewState = LogInViewState()

    // TODO: Implement the ViewModel
}