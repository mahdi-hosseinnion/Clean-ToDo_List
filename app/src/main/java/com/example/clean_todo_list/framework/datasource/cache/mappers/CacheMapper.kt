package com.example.clean_todo_list.framework.datasource.cache.mappers

import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.util.EntityMapper
import com.example.clean_todo_list.framework.datasource.cache.model.TaskCacheEntity

object CacheMapper : EntityMapper<TaskCacheEntity, Task>() {

    override fun mapEntityToDomainModel(entity: TaskCacheEntity): Task = Task(
        id = entity.id,
        title = entity.title,
        body = entity.body,
        isDone = entity.isDone,
        updated_at = entity.updated_at,
        created_at = entity.created_at,
    )

    override fun mapDomainModelToEntity(domainModel: Task): TaskCacheEntity = TaskCacheEntity(
        id = domainModel.id,
        title = domainModel.title,
        body = domainModel.body,
        isDone = domainModel.isDone,
        updated_at = domainModel.updated_at,
        created_at = domainModel.created_at,
    )
}