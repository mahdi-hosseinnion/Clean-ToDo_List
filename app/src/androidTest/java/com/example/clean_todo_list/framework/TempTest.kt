package com.example.clean_todo_list.framework

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.clean_todo_list.di.TestAppComponent
import com.example.clean_todo_list.framework.presentation.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class TempTest {

    val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext<Context>() as TestBaseApplication

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    init {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }

    @Test
    fun someRandomTest() {

        assert(::firebaseFirestore.isInitialized)
    }
}