package com.example.clean_todo_list.framework.presentation.tasklist

import android.os.Bundle
import android.view.View
import com.example.clean_todo_list.R
import com.example.clean_todo_list.framework.presentation.common.BaseTaskFragment

class TaskListFragment : BaseTaskFragment(R.layout.fragment_task_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inject() {
//        TODO("prepare dagger")
    }

}
