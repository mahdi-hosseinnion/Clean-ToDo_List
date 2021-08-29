package com.example.clean_todo_list.framework.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clean_todo_list.R
import com.example.clean_todo_list.business.interactors.auth.login.LoginUser
import com.example.clean_todo_list.databinding.FragmentLogInBinding
import com.example.clean_todo_list.framework.presentation.common.BaseFragment
import com.example.clean_todo_list.util.toastLong
import com.example.clean_todo_list.util.toastShort
import com.google.firebase.auth.FirebaseAuth
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

    private fun subscribeObservers() {
        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner) {
            uiController.displayProgressBar(it)
        }
        viewModel.stateMessage.observe(viewLifecycleOwner) {
            it?.let {
                if (it.response.message == LoginUser.LOGIN_USER_SUCCESS) {
                    navToApp()
                }
                toastLong(it.response.message ?: "")
                viewModel.clearStateMessage()
            }
        }
    }

    private fun setupUi() {
        binding.loginLoginWithTestUserTxt.setOnClickListener {
            binding.loginLoginWithTestUserTxt.isEnabled = false
            tryToSignInIntoFirestore()
        }
        binding.loginLoginBtn.setOnClickListener {
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
    }

    private fun navToForgotPasswordFragment() {
        findNavController().navigate(R.id.action_logInFragment_to_forgotPasswordFragment)
    }

    private fun navToSignUpFragment() {
        findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
    }

    private fun tryToSignInIntoFirestore() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            EMAIL, PASSWORD
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                navToApp()
            } else {
                binding.loginLoginWithTestUserTxt.isEnabled = true
            }
        }
    }

    private fun navToApp() {
        findNavController().navigate(R.id.action_logInFragment_to_splashFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val EMAIL = "testReleaseUser@cleanTodo.com"
        private const val PASSWORD = "123456789"
    }

}