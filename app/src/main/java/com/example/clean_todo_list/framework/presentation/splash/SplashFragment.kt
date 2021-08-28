package com.example.clean_todo_list.framework.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clean_todo_list.R
import com.example.clean_todo_list.databinding.FragmentSplashBinding
import com.example.clean_todo_list.framework.presentation.common.BaseFragment
import com.example.clean_todo_list.framework.presentation.utils.gone
import com.example.clean_todo_list.framework.presentation.utils.visible
import com.example.clean_todo_list.util.printLogD
import com.google.firebase.auth.FirebaseAuth

class SplashFragment(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseFragment() {

    private var _binding: FragmentSplashBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    val viewModel: SplashViewModel by viewModels { viewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkFirebaseAuth()
    }


    private fun checkFirebaseAuth() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            navToLoginFragment()
        } else {
            onUserSyncIn()
        }
    }

    private fun onUserSyncIn() {
        viewModel.performSync()
        viewModel.hasSyncBeenExecuted.observe(viewLifecycleOwner) {
            if (it) {
                navToNoteListFragment()
            }
        }
    }

    private fun navToLoginFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_logInFragment)
    }


    private fun navToNoteListFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_taskListFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
