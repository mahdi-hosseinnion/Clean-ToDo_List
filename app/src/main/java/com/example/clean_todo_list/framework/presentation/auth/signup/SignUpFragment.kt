package com.example.clean_todo_list.framework.presentation.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clean_todo_list.R
import com.example.clean_todo_list.business.domain.state.StateMessageCallback
import com.example.clean_todo_list.business.interactors.auth.signup.SignUpUser
import com.example.clean_todo_list.databinding.FragmentSignUpBinding
import com.example.clean_todo_list.framework.presentation.common.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class SignUpFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseFragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    val viewModel: SignUpViewModel by viewModels { viewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        subscribeObservers()

    }

    private fun setupUi() {
        binding.signupSignupBtn.setOnClickListener {
            checkIfInsertedDataIsValidThenSignUp()
        }
        binding.signupLoginBtn.setOnClickListener {
            navigateBack()
        }
    }

    private fun subscribeObservers() {
        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner) {
            it.let {
                uiController.displayProgressBar(it)
            }
        }
        viewModel.stateMessage.observe(viewLifecycleOwner) { st ->
            st?.let { stateMessage ->
                uiController.onResponseReceived(
                    stateMessage.response,
                    object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    })
                if (stateMessage.response.message == SignUpUser.SIGNUP_SUCCESS) {
                    navToApp()
                }
            }
        }
    }

    private fun navToApp() {
        findNavController().navigate(R.id.action_signUpFragment_to_splashFragment)
    }

    private fun checkIfInsertedDataIsValidThenSignUp() {
        val email = binding.signupEmailEdt.text.toString()
        val password = binding.signupPasswordEdt.text.toString()
        val confirmPassword = binding.signupConfirmPasswordEdt.text.toString()

        if (password != confirmPassword) {
            binding.signupConfirmPasswordEdt.error = getString(R.string.passwords_does_not_match)
            return
        }
        viewModel.signUp(
            email,
            password
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}