package com.example.clean_todo_list.framework.presentation.utils

import com.example.clean_todo_list.business.domain.state.DialogInputCaptureCallback
import com.example.clean_todo_list.business.domain.state.Response
import com.example.clean_todo_list.business.domain.state.StateMessageCallback


interface UIController {

    fun displayProgressBar(isDisplayed: Boolean)

    fun hideSoftKeyboard()

    fun displayInputCaptureDialog(title: String, callback: DialogInputCaptureCallback)

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

}


















