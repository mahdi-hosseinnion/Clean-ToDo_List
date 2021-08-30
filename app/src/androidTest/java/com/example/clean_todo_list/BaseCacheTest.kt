package com.example.clean_todo_list


import com.example.clean_todo_list.framework.datasource.cache.database.TaskDao
import com.example.clean_todo_list.framework.datasource.cache.mappers.CacheMapper
import com.example.clean_todo_list.framework.datasource.data.TaskDataFactory
import kotlinx.coroutines.runBlocking
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