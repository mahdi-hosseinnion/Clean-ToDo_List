package com.example.clean_todo_list.framework.presentation.common

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.example.clean_todo_list.framework.presentation.splash.SplashFragment
import com.example.clean_todo_list.framework.presentation.taskdetail.TaskDetailFragment
import com.example.clean_todo_list.framework.presentation.tasklist.TaskListFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class TaskFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            TaskListFragment::class.java.name -> {
                TaskListFragment(viewModelFactory)

            }

            TaskDetailFragment::class.java.name -> {
                TaskDetailFragment(viewModelFactory)

            }

            SplashFragment::class.java.name -> {
                SplashFragment(viewModelFactory)

            }

            else -> {
                super.instantiate(classLoader, className)
            }
        }
}