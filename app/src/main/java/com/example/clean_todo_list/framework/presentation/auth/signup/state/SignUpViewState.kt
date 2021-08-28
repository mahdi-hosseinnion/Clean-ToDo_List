package com.example.clean_todo_list.framework.presentation.auth.signup.state

import android.os.Parcelable
import com.example.clean_todo_list.business.domain.state.ViewState
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpViewState(
    val email: String? = null,
    val password: String? = null,
    val repeatPassword: String? = null
) : Parcelable, ViewState
