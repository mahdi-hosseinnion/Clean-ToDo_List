package com.example.clean_todo_list.framework.presentation.tododetail

import android.os.Bundle
import android.view.View
import com.example.clean_todo_list.R
import com.example.clean_todo_list.framework.presentation.common.BaseTodoFragment

const val TODO_DETAIL_STATE_BUNDLE_KEY = "com.example.clean_todo_list.framework.presentation.tododetail.state"

class TodoDetailFragment : BaseTodoFragment(R.layout.fragment_todo_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inject() {
//        TODO("prepare dagger")
    }
}