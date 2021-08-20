package com.example.clean_todo_list.framework.presentation.taskdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.clean_todo_list.R
import com.example.clean_todo_list.framework.presentation.common.BaseTaskFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

const val TASK_DETAIL_STATE_BUNDLE_KEY =
    "com.example.clean_todo_list.framework.presentation.taskdetail.state"

@FlowPreview
@ExperimentalCoroutinesApi
class TaskDetailFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseTaskFragment(
    R.layout.fragment_task_detail
) {

    val viewModel: TaskDetailViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inject() {
//        TODO("prepare dagger")
    }
}