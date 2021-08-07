package com.example.clean_todo_list.framework.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clean_todo_list.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}