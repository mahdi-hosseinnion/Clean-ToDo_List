package com.example.clean_todo_list.framework.datasource.cache.implementation

import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.util.DateUtil
import com.example.clean_todo_list.framework.datasource.cache.abstraction.TaskDaoService
import com.example.clean_todo_list.framework.datasource.cache.database.TaskDao
import com.example.clean_todo_list.framework.datasource.cache.database.observeOrderedQuery
import com.example.clean_todo_list.framework.datasource.cache.database.returnOrderedQuery
import com.example.clean_todo_list.framework.datasource.cache.mappers.CacheMapper
import com.example.clean_todo_list.framework.datasource.cache.util.FilterAndOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskDaoServiceImpl
@Inject
constructor(
    private val taskDao: TaskDao
) : TaskDaoService {

    override suspend fun insertTask(task: Task): Long =
        taskDao.insertTask(CacheMapper.mapDomainModelToEntity(task))

    override suspend fun insertTasks(tasks: List<Task>): LongArray =
        taskDao.insertTasks(CacheMapper.mapDomainModelListToEntityList(tasks))

    override suspend fun deleteTask(primaryKey: String): Int =
        taskDao.deleteTask(primaryKey)

    override suspend fun deleteTasks(tasks: List<Task>): Int =
        taskDao.deleteTasks(tasks.mapIndexed { _, task -> task.id })

    override suspend fun updateTask(
        primaryKey: String,
        newTitle: String,
        newBody: String,
        newIsDone: Boolean,
        updated_at: Long
    ): Int = taskDao.updateTask(
        primaryKey,
        newTitle,
        newBody,
        newIsDone,
        updated_at
    )


    override suspend fun updateIsDone(primaryKey: String, isDone: Boolean): Int =
        taskDao.updateIsDone(
            primaryKey = primaryKey,
            isDone = isDone,
            updated_at = DateUtil.getCurrentTimestamp()
        )

    override suspend fun getAllTasks(): List<Task> = CacheMapper.mapEntityListToDomainModelList(
        taskDao.getAllTasks()
    )

    override suspend fun searchTaskById(primaryKey: String): Task? =
        taskDao.getTaskById(primaryKey)?.let {
            CacheMapper.mapEntityToDomainModel(it)
        }

    override suspend fun getNumOfTasks(): Int =
        taskDao.getNumOfTasks()


    override suspend fun searchTasksOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Task> = CacheMapper.mapEntityListToDomainModelList(
        taskDao.searchTasksOrderByDateDESC(query, page, pageSize)
    )

    override suspend fun searchTasksOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Task> = CacheMapper.mapEntityListToDomainModelList(
        taskDao.searchTasksOrderByDateASC(query, page, pageSize)
    )


    override suspend fun searchTasksOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Task> = CacheMapper.mapEntityListToDomainModelList(
        taskDao.searchTasksOrderByTitleDESC(query, page, pageSize)
    )


    override suspend fun searchTasksOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Task> = CacheMapper.mapEntityListToDomainModelList(
        taskDao.searchTasksOrderByTitleASC(query, page, pageSize)
    )


    override suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: FilterAndOrder,
        page: Int
    ): List<Task> = CacheMapper.mapEntityListToDomainModelList(
        taskDao.returnOrderedQuery(query = query, filterAndOrder = filterAndOrder, page = page)
    )

    override fun observeOrderedQuery(
        query: String,
        filterAndOrder: FilterAndOrder,
        page: Int
    ): Flow<List<Task>> = CacheMapper.mapEntityFlowToDomainModelFlow(
        taskDao.observeOrderedQuery(query = query, filterAndOrder = filterAndOrder, page = page)
    )
}

