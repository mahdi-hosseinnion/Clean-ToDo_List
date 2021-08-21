package com.example.clean_todo_list.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toastShort( text: String) {
    Toast.makeText(this.requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastLong(text: String) {
    Toast.makeText(this.requireContext(), text, Toast.LENGTH_LONG).show()
}