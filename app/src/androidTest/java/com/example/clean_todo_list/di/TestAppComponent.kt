package com.example.clean_todo_list.di

import com.example.clean_todo_list.framework.datasource.cache.TaskDaoServiceTest
import com.example.clean_todo_list.framework.datasource.network.TaskFirestoreServiceTest
import com.example.clean_todo_list.framework.presentation.TestBaseApplication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        TestModule::class
    ]
)
interface TestAppComponent : AppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance testBaseApplication: TestBaseApplication): TestAppComponent

    }

    fun inject(taskFirestoreServiceTest: TaskFirestoreServiceTest)

    fun inject(taskDaoServiceTest: TaskDaoServiceTest)
}