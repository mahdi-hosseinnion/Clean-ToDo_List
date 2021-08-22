package com.example.clean_todo_list.framework.presentation.taskdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.clean_todo_list.R
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.MessageType
import com.example.clean_todo_list.business.domain.state.Response
import com.example.clean_todo_list.business.domain.state.StateMessage
import com.example.clean_todo_list.business.domain.state.UIComponentType
import com.example.clean_todo_list.databinding.FragmentTaskDetailBinding
import com.example.clean_todo_list.framework.presentation.common.BaseTaskFragment
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent
import com.example.clean_todo_list.util.Constants.TAG
import com.example.clean_todo_list.util.cLog
import com.example.clean_todo_list.util.printLogD
import com.example.clean_todo_list.util.toastLong
import com.example.clean_todo_list.util.toastShort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

const val TASK_DETAIL_STATE_BUNDLE_KEY =
    "com.example.clean_todo_list.framework.presentation.taskdetail.state"
const val TASK_DETAIL_SELECTED_TASK_BUNDLE_KEY =
    "SELECTED_TASK_BUNDLE_KEY_FOR_BUNDLE"

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
        getSelectedTaskFromPreviousFragment()
        subscribeObserves()
    }

    private fun subscribeObserves() {
        viewModel.viewState.observe(viewLifecycleOwner) { vs ->
            vs?.let { viewState ->
                viewState.task?.let {
                    setTaskData(it)
                }
            }
        }
        viewModel.stateMessage.observe(viewLifecycleOwner) { stateMessage ->
            toastShort(stateMessage?.response?.message ?: "")
        }
    }

    private fun setTaskData(task: Task) {
        binding.titleDetail.setText(task.title)
        binding.bodyDetail.setText(task.body)
        binding.isDoneDetail.isChecked = task.isDone

    }


    private fun getSelectedTaskFromPreviousFragment() {
        arguments?.let { args ->
            (args.getParcelable(TASK_DETAIL_SELECTED_TASK_BUNDLE_KEY) as Task?)?.let { selectedTask ->
                viewModel.setTask(selectedTask)

            } ?: onErrorRetrievingTaskFromPreviousFragment()
        } ?: onErrorRetrievingTaskFromPreviousFragment()
    }

    private fun onErrorRetrievingTaskFromPreviousFragment() {
        viewModel.setStateEvent(
            TaskListStateEvent.CreateStateMessageEvent(
                stateMessage = StateMessage(
                    response = Response(
                        message = getString(R.string.error_retrieving_task_from_bundle),
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Error
                    )
                )
            )
        )
        cLog(
            TAG,
            "onErrorRetrievingTaskFromPreviousFragment: Error retrieving selected note from bundle."
        )
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