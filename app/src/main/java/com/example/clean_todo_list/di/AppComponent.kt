package com.example.clean_todo_list.di

import com.example.clean_todo_list.framework.presentation.BaseApplication
import com.example.clean_todo_list.framework.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
@Component(
    modules = [
        AppModule::class,
        ProductionModule::class,
        TaskViewModelModule::class,
        TaskFragmentFactoryModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance baseApplication: BaseApplication): AppComponent

    }

    fun inject(mainActivity: MainActivity)

}