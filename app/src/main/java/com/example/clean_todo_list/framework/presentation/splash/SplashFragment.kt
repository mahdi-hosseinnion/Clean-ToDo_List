package com.example.clean_todo_list.framework.presentation.splash

import android.os.Bundle
import android.view.View
import com.example.clean_todo_list.R
import com.example.clean_todo_list.framework.presentation.common.BaseTaskFragment

class SplashFragment: BaseTaskFragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inject() {
//        TODO("prepare dagger")
    }

}
