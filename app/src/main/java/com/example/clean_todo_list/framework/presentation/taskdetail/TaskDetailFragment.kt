package com.example.clean_todo_list.framework.presentation.taskdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.clean_todo_list.R
import com.example.clean_todo_list.databinding.FragmentTaskDetailBinding
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
) {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    val viewModel: TaskDetailViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}