package com.example.clean_todo_list.framework.presentation.auth.login.state

import android.os.Parcelable
import com.example.clean_todo_list.business.domain.state.ViewState
import kotlinx.parcelize.Parcelize

@Parcelize
data class LogInViewState(
    val email: String? = null,
    val password: String? = null
) : Parcelable, ViewState
