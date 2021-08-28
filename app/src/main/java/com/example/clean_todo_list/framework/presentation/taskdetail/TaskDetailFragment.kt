package com.example.clean_todo_list.framework.presentation.taskdetail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.clean_todo_list.R
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.*
import com.example.clean_todo_list.business.interactors.common.DeleteTask.Companion.DELETE_TASK_SUCCESS
import com.example.clean_todo_list.business.interactors.taskdetail.UpdateTask.Companion.UPDATE_TASK_SUCCESS
import com.example.clean_todo_list.databinding.FragmentTaskDetailBinding
import com.example.clean_todo_list.framework.presentation.common.BaseFragment
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent
import com.example.clean_todo_list.util.Constants.TAG
import com.example.clean_todo_list.util.cLog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect

const val TASK_DETAIL_STATE_BUNDLE_KEY =
    "com.example.clean_todo_list.framework.presentation.taskdetail.state"
const val TASK_DETAIL_SELECTED_TASK_BUNDLE_KEY =
    "SELECTED_TASK_BUNDLE_KEY_FOR_BUNDLE"

@FlowPreview
@ExperimentalCoroutinesApi
class TaskDetailFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseFragment(
) {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    val viewModel: TaskDetailViewModel by viewModels { viewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        if (viewModel.doesNotContainTask()) {
            getSelectedTaskFromPreviousFragment()
        }
        subscribeObserves()
    }

    private fun setupUi() {
        binding.doneFab.hide()

        binding.titleDetail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != viewModel.viewState.value?.task?.title) {
                    viewModel.setTitle(p0.toString())
                }
            }
        })
        binding.bodyDetail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != viewModel.viewState.value?.task?.body) {
                    viewModel.setBody(p0.toString())
                }
            }
        })
        //onClicks
        binding.deleteTaskBtn.setOnClickListener {
            showAreYouSureDialogForDelete()
        }

        binding.doneFab.setOnClickListener {
            viewModel.updateTask()
        }

        binding.detailBackBtn.setOnClickListener {
            uiController.hideSoftKeyboard()
            navigateBack()
        }

        binding.isDoneDetail.setOnCheckedChangeListener { _, checked ->
            if (viewModel.getTaskIsDone() != checked) {
                viewModel.updateIsDone(checked)
                viewModel.setTaskIsDone(checked)
            }
        }

    }

    private fun subscribeObserves() {
        viewModel.viewState.observe(viewLifecycleOwner) { vs ->
            vs?.let { viewState ->
                viewState.task?.let {
                    setTaskData(it)
                }
            }
        }
        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner) {
            it?.let {
                uiController.displayProgressBar(it)
            }
        }
        val stateMessageCallback = object : StateMessageCallback {
            override fun removeMessageFromStack() {
                viewModel.clearStateMessage()
            }
        }
        viewModel.stateMessage.observe(viewLifecycleOwner) { stateMessage ->

            stateMessage?.response?.let { response ->
                when (response.message) {

                    DELETE_TASK_SUCCESS -> {
                        navigateBack()
                    }
                    UPDATE_TASK_SUCCESS -> {
                        uiController.hideSoftKeyboard()
                        navigateBack()
                    }
                    else -> {
                        uiController.onResponseReceived(
                            response = response,
                            stateMessageCallback = stateMessageCallback
                        )
                    }
                }

            }
        }
        //https://youtu.be/B8ppnjGPAGE?t=1101
        lifecycleScope.launchWhenStarted {
            viewModel.shouldDisplaySaveEditButton.collect { shouldDisplaySaveEditButton ->
                if (shouldDisplaySaveEditButton) {
                    binding.doneFab.show()
                } else {
                    binding.doneFab.hide()
                }
            }
        }

    }

    private fun setTaskData(task: Task) {
        if (binding.titleDetail.text.toString() != task.title) {
            binding.titleDetail.setText(task.title)
        }
        if (binding.bodyDetail.text.toString() != task.body) {
            binding.bodyDetail.setText(task.body)
        }
        if (binding.isDoneDetail.isChecked != task.isDone) {
            binding.isDoneDetail.isChecked = task.isDone
        }

    }


    private fun getSelectedTaskFromPreviousFragment() {
        arguments?.let { args ->
            (args.getParcelable(TASK_DETAIL_SELECTED_TASK_BUNDLE_KEY) as Task?)?.let { selectedTask ->
                viewModel.setOriginalTask(selectedTask)

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


    private fun showAreYouSureDialogForDelete() {
        val callback = object : AreYouSureCallback {
            override fun proceed() {
                viewModel.deleteTask()
            }

            override fun cancel() {}
        }
        viewModel.setStateEvent(
            TaskListStateEvent.CreateStateMessageEvent(
                stateMessage = StateMessage(
                    response = Response(
                        message = getString(R.string.are_you_sure_delete),
                        uiComponentType = UIComponentType.AreYouSureDialog(
                            callback = callback
                        ),
                        messageType = MessageType.Info
                    )
                )
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}