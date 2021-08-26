package com.example.clean_todo_list.framework.presentation.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clean_todo_list.R
import com.example.clean_todo_list.databinding.BottomSheetChangeFilterBinding
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder
import com.example.clean_todo_list.framework.datasource.cache.util.SortAndOrder.*
import com.example.clean_todo_list.util.cLog
import com.example.clean_todo_list.util.toastLong
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangeSortBottomSheet(
    private val defaultSortAndOrder: SortAndOrder,
    private val onChangeSort: (selectedSort: SortAndOrder) -> Unit
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
        binding.sortCreateDate.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                setOrderTextForDate()
            }
        }
        binding.sortName.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                setOrderTextForName()
            }
        }
        when (defaultSortAndOrder) {
            CREATED_DATE_DESC -> {
                binding.sortCreateDate.isChecked = true
                binding.orderDesc.isChecked = true
            }
            CREATED_DATE_ASC -> {
                binding.sortCreateDate.isChecked = true
                binding.orderAsc.isChecked = true
            }
            NAME_DESC -> {
                binding.sortName.isChecked = true
                binding.orderDesc.isChecked = true
            }
            NAME_ACS -> {
                binding.sortName.isChecked = true
                binding.orderAsc.isChecked = true
            }
        }


        binding.applyBtn.setOnClickListener {
            onApplyClicked(
                sortCreatedAt = binding.sortCreateDate.isChecked,
                sortName = binding.sortName.isChecked,
                orderACS = binding.orderAsc.isChecked,
                orderDESC = binding.orderDesc.isChecked
            )
        }
    }

    private fun setOrderTextForName() {
        binding.ascOrDescTxt.text = getString(R.string.a_to_z_or_z_to_a)
        binding.orderAsc.text = getString(R.string.a_to_z)
        binding.orderDesc.text = getString(R.string.z_to_a)
    }

    private fun setOrderTextForDate() {
        binding.ascOrDescTxt.text = getString(R.string.newer_to_older_or_older_to_newer)
        binding.orderAsc.text = getString(R.string.older_to_newer)
        binding.orderDesc.text = getString(R.string.newer_to_older)
    }

    private fun onApplyClicked(
        sortCreatedAt: Boolean,
        sortName: Boolean,
        orderACS: Boolean,
        orderDESC: Boolean
    ) {
        var result: SortAndOrder? = null

        if (sortName) {
            if (orderACS) {
                result = NAME_ACS
            } else if (orderDESC) {
                result = NAME_DESC
            }
        } else if (sortCreatedAt) {
            if (orderACS) {
                result = CREATED_DATE_ASC
            } else if (orderDESC) {
                result = CREATED_DATE_DESC
            }
        }
        if (result != null) {
            onChangeSort(result)
            dismiss()
        } else {
            toastLong(getString(R.string.change_sort_bottom_sheet_error))
            cLog(
                "NULL FINAL RESULT with " +
                        "sortCreatedAt: $sortCreatedAt, sortName: $sortName, " +
                        "orderACS: $orderACS, orderDESC: $orderDESC ", "$TAG, onApplyClicked"
            )
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