package com.example.clean_todo_list.framework.presentation.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clean_todo_list.databinding.BottomSheetChangeFilterBinding
import com.example.clean_todo_list.framework.datasource.cache.util.FilterAndOrder
import com.example.clean_todo_list.framework.datasource.cache.util.FilterAndOrder.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangeFilterBottomSheet(
    val defaultFilterAndOrder: FilterAndOrder
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetChangeFilterBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetChangeFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        binding.bottomSheetDismissBtn.setOnClickListener {
            dismiss()
        }
        when (defaultFilterAndOrder) {
            DATE_DESC -> {
                binding.sortCreateDate.isChecked = true
                binding.orderDesc.isChecked = true
            }
            DATE_ASC -> {
                binding.sortCreateDate.isChecked = true
                binding.orderAsc.isChecked = true
            }
            TITLE_DESC -> {
                binding.sortName.isChecked = true
                binding.orderDesc.isChecked = true
            }
            TITLE_ACS -> {
                binding.sortName.isChecked = true
                binding.orderAsc.isChecked = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ChangeFilterBottomSheet"
    }
}