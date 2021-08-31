package com.example.clean_todo_list.framework.presentation.auth.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.clean_todo_list.business.domain.state.StateMessageCallback
import com.example.clean_todo_list.databinding.FragmentForgotPasswordBinding
import com.example.clean_todo_list.framework.presentation.common.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class ForgotPasswordFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseFragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    val viewModel: ForgotPasswordViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner) {
            it?.let {
                uiController.displayProgressBar(it)
            }
        }
        viewModel.stateMessage.observe(viewLifecycleOwner) { st ->
            st?.let { stateMessage ->
                uiController.onResponseReceived(
                    response = stateMessage.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    })

            }
        }
    }

    private fun setupUi() {
        binding.submitBtn.setOnClickListener {
            sendResetPasswordEmail()
        }
        binding.forgotPasswordBackToLoginTxt.setOnClickListener {
            navigateUp()
        }
    }

    private fun sendResetPasswordEmail() {
        viewModel.sendResetPasswordEmail(
            binding.forgotPasswordEmailEdt.text.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}