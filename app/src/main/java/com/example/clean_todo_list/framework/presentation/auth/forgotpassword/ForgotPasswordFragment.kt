package com.example.clean_todo_list.framework.presentation.auth.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clean_todo_list.databinding.FragmentForgotPasswordBinding
import com.example.clean_todo_list.framework.presentation.common.BaseFragment
import com.example.clean_todo_list.util.toastShort

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
    }

    private fun setupUi() {
        binding.signupSignupBtn.setOnClickListener {
            toastShort("Forgot password system is not ready!")
        }
        binding.forgotPasswordBackToLoginTxt.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}