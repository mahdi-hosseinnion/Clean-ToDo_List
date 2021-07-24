package com.example.clean_todo_list.framework.presentation.taskdetail

import android.os.Bundle
import android.view.View
import com.example.clean_todo_list.R
import com.example.clean_todo_list.framework.presentation.common.BaseTaskFragment

const val TASK_DETAIL_STATE_BUNDLE_KEY = "com.example.clean_todo_list.framework.presentation.taskdetail.state"

class TaskDetailFragment : BaseTaskFragment(R.layout.fragment_task_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inject() {
//        TODO("prepare dagger")
    }
}