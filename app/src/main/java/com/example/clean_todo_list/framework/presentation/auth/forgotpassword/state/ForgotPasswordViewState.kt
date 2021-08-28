package com.example.clean_todo_list.framework.presentation.auth.forgotpassword.state

import android.os.Parcelable
import com.example.clean_todo_list.business.domain.state.ViewState
import kotlinx.parcelize.Parcelize

@Parcelize
data class ForgotPasswordViewState(
    val email: String? = null
) : Parcelable, ViewState
