package com.example.clean_todo_list


import com.example.clean_todo_list.framework.datasource.cache.database.TaskDao
import com.example.clean_todo_list.framework.datasource.cache.mappers.CacheMapper
import com.example.clean_todo_list.framework.datasource.data.TaskDataFactory
import com.example.clean_todo_list.framework.datasource.network.implemetation.TaskFirestoreServiceImpl.Companion.TASKS_COLLECTION
import com.example.clean_todo_list.framework.datasource.network.implemetation.TaskFirestoreServiceImpl.Companion.USER_ID
import com.example.clean_todo_list.framework.datasource.network.mappers.NetworkMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

abstract class BaseCacheTest : BaseTest() {

    @Inject
    lateinit var taskDao: TaskDao

    @Inject
    lateinit var taskDataFactory: TaskDataFactory


    fun insertTestData() = runBlocking {
        val data =
            CacheMapper.mapDomainModelListToEntityList(
                taskDataFactory.produceListOfTasks()
            )
        taskDao.insertTasks(data)
    }

}