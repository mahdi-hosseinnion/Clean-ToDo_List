package com.example.clean_todo_list.framework.presentation.tasklist

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clean_todo_list.R
import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.state.DialogInputCaptureCallback
import com.example.clean_todo_list.databinding.FragmentTaskListBinding
import com.example.clean_todo_list.framework.presentation.common.BaseTaskFragment
import com.example.clean_todo_list.framework.presentation.taskdetail.TASK_DETAIL_SELECTED_TASK_BUNDLE_KEY
import com.example.clean_todo_list.framework.presentation.taskdetail.TaskListViewModel
import com.example.clean_todo_list.framework.presentation.tasklist.state.TaskListStateEvent.*
import com.example.clean_todo_list.util.printLogD
import com.example.clean_todo_list.util.toastShort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TaskListFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseTaskFragment(), TaskListAdapter.Interaction, CustomSearchAndFilterView.Interaction {

    private var _binding: FragmentTaskListBinding? = null

    private val binding get() = _binding!!

    val viewModel: TaskListViewModel by viewModels { viewModelFactory }

    var listAdapter: TaskListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupUI()
        setupRecyclerView()
        setupFAB()
        setupSwipeRefresh()
        startNewSearch()
        subscribeObservers()

    }

    private fun setupUI() {
        binding.searchAndFilterView.interaction = this
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            searchBackStackCallback
        )
    }

    private val searchBackStackCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.searchAndFilterView.onBackClicked()
        }

    }

    private fun setupRecyclerView() {

        binding.taskRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TaskListFragment.requireActivity())

            val topSpacingDecorator = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecorator)

            listAdapter = TaskListAdapter(this@TaskListFragment)
            //pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == listAdapter?.itemCount?.minus(1)) {
                        viewModel.nextPage()
                    }
                }
            })
            adapter = listAdapter
        }
    }

    private fun setupFAB() {
        binding.insertNewTaskFab.setOnClickListener {
            val callback = object : DialogInputCaptureCallback {
                override fun onTextCaptured(text: String) {
                    if (text.isNotBlank()) {
                        //start insertion
                        viewModel.setStateEvent(
                            InsertNewTaskEvent(
                                title = text
                            )
                        )
                    }
                }
            }
            uiController.displayInputCaptureDialog(
                getString(R.string.enter_title),
                callback
            )
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) { vs ->
            vs?.let { viewState ->
            }
        }
        viewModel.items.observe(viewLifecycleOwner) {
            listAdapter?.submitList(it)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            startNewSearch()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.retrieveNumTasksInCache()
        viewModel.clearList()
        viewModel.refreshSearchQuery()

    }

    override fun onPause() {
        super.onPause()
        saveLayoutManagerState()
    }

    private fun startNewSearch() {
        viewModel.clearList()
        viewModel.loadFirstPage()
    }

    override fun onItemSelected(position: Int, item: Task) {
        printLogD("onItemSelected", "clicked with position: $position \n item title: ${item.title}")
        //TODO consider using only item id to pass it instead of bundle
        val bundle = bundleOf(TASK_DETAIL_SELECTED_TASK_BUNDLE_KEY to item)
        findNavController().navigate(
            R.id.action_taskListFragment_to_taskDetailFragment,
            bundle
        )
    }

    override fun onChangeIsDoneSelected(taskId: String, newIsDone: Boolean, title: String?) {
        printLogD(
            "onChangeIsDoneSelected",
            "clicked with title: $title \n newIsDone: ${newIsDone} \n taskId: $taskId"
        )
        viewModel.setStateEvent(ChangeTaskDoneStateEvent(taskId, newIsDone))
    }

    private fun saveLayoutManagerState() {
        binding.taskRecyclerView.layoutManager?.onSaveInstanceState()?.let { lmState ->
            viewModel.setLayoutManagerState(lmState)
        }
    }


    override fun forceKeyBoardToOpenForEditText(editText: EditText) {
        editText.requestFocus()
        val imm: InputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun hideSoftKeyboard() {
        uiController.hideSoftKeyboard()
    }

    override fun onFilterButtonClicked() {
        toastShort("FilterButtonClicked")
//        TODO("Not yet implemented")
    }

    override fun onSearchTextChanged(text: String) {
        viewModel.setQuery(text)
    }

    override fun onSearchStateChanged(newState: CustomSearchAndFilterView.SearchViewState) =
        when (newState) {
            is CustomSearchAndFilterView.SearchViewState.VISIBLE -> {
                searchBackStackCallback.isEnabled = true
            }
            is CustomSearchAndFilterView.SearchViewState.INVISIBLE -> {
                searchBackStackCallback.isEnabled = false
            }

        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
