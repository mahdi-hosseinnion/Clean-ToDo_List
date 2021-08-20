package com.example.clean_todo_list.framework.presentation.tasklist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.clean_todo_list.R
import com.example.clean_todo_list.framework.presentation.common.BaseTaskFragment
import com.example.clean_todo_list.framework.presentation.taskdetail.TaskListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TaskListFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseTaskFragment(
    R.layout.fragment_task_list
) {

    val viewModel: TaskListViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inject() {
//        TODO("prepare dagger")
    }

}
