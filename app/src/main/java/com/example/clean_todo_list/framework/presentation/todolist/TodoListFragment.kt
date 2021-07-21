package com.example.clean_todo_list.framework.presentation.todolist

import android.os.Bundle
import android.view.View
import com.example.clean_todo_list.R
import com.example.clean_todo_list.framework.presentation.common.BaseTodoFragment

class NoteListFragment : BaseTodoFragment(R.layout.fragment_todo_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inject() {
//        TODO("prepare dagger")
    }

}
