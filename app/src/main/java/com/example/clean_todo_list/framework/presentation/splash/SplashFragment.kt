package com.example.clean_todo_list.framework.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.clean_todo_list.R
import com.example.clean_todo_list.framework.presentation.common.BaseTaskFragment

class SplashFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseTaskFragment(
    R.layout.fragment_splash
) {

    val viewModel: SplashViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inject() {
//        TODO("prepare dagger")
    }

}
