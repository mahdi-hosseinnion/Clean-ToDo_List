package com.example.clean_todo_list.framework.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clean_todo_list.R
import com.example.clean_todo_list.business.domain.state.MessageType
import com.example.clean_todo_list.business.domain.state.StateMessageCallback
import com.example.clean_todo_list.business.interactors.auth.login.LoginUser
import com.example.clean_todo_list.databinding.FragmentLogInBinding
import com.example.clean_todo_list.framework.presentation.common.BaseFragment
import com.example.clean_todo_list.framework.presentation.utils.disable
import com.example.clean_todo_list.framework.presentation.utils.enable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class LogInFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseFragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    val viewModel: LogInViewModel by viewModels { viewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        subscribeObservers()
    }

    private fun setupUi() {
        binding.loginLoginBtn.setOnClickListener {
            binding.loginLoginBtn.disable()
            viewModel.login(
                binding.loginEmailEdt.text.toString(),
                binding.loginPasswordEdt.text.toString()
            )
        }
        binding.loginForgotPasswordTxt.setOnClickListener {
            navToForgotPasswordFragment()
        }
        binding.loginSignupTxt.setOnClickListener {
            navToSignUpFragment()
        }
        binding.loginEmailEdt.addTextChangedListener {
            viewModel.setEmail(it.toString())
        }
        binding.loginPasswordEdt.addTextChangedListener {
            viewModel.setPassword(it.toString())
        }
    }

    private fun subscribeObservers() {
        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner) {
            uiController.displayProgressBar(it)
        }
        viewModel.stateMessage.observe(viewLifecycleOwner) {
            it?.let {
                if (it.response.message == LoginUser.LOGIN_USER_SUCCESS) {
                    navToApp()
                }
                if (it.response.messageType != MessageType.Success) {
                    binding.loginLoginBtn.enable()
                }
                uiController.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        }
        viewModel.viewState.observe(viewLifecycleOwner) { vs ->
            vs?.let { logInViewState ->
                logInViewState.email?.let { setEmail(it) }
                logInViewState.password?.let { setPassword(it) }
            }
        }
    }

    private fun setEmail(email: String) {
        if (email != binding.loginEmailEdt.text.toString()) {
            binding.loginEmailEdt.setText(email)
        }
    }

    private fun setPassword(password: String) {
        if (password != binding.loginPasswordEdt.text.toString()) {
            binding.loginPasswordEdt.setText(password)
        }
    }


    private fun navToForgotPasswordFragment() {
        findNavController().navigate(R.id.action_logInFragment_to_forgotPasswordFragment)
    }

    private fun navToSignUpFragment() {
        findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
    }

    private fun navToApp() {
        findNavController().navigate(R.id.action_logInFragment_to_splashFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}