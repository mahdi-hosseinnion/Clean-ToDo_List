package com.example.clean_todo_list.framework.presentation.common

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.clean_todo_list.framework.presentation.MainActivity
import com.example.clean_todo_list.framework.presentation.utils.UIController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
abstract class BaseFragment : Fragment() {

    lateinit var uiController: UIController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setUIController(null) // null in production
    }

    private fun setUIController(mockController: UIController?) {

        // TEST: Set interface from mock
        if (mockController != null) {
            this.uiController = mockController
        } else { // PRODUCTION: if no mock, get from context
            activity?.let {
                if (it is MainActivity) {
                    try {
                        uiController = context as UIController
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    //util functions

    fun navigateUp() {
        findNavController().navigateUp()
    }


}