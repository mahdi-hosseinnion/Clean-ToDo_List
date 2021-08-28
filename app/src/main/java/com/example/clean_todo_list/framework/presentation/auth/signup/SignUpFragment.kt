package com.example.clean_todo_list.framework.presentation.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clean_todo_list.databinding.FragmentSignUpBinding
import com.example.clean_todo_list.framework.presentation.common.BaseFragment
import com.example.clean_todo_list.util.toastShort

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

    }

    private fun setupUi() {
        binding.signupSignupBtn.setOnClickListener {
            toastShort("Sign up system is not ready!")
        }
        binding.signupLoginBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}