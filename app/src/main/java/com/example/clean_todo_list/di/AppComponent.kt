package com.example.clean_todo_list.di

import com.example.clean_todo_list.framework.presentation.BaseApplication
import com.example.clean_todo_list.framework.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        ProductionModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance baseApplication: BaseApplication): AppComponent

    }

    fun inject(mainActivity: MainActivity)

}