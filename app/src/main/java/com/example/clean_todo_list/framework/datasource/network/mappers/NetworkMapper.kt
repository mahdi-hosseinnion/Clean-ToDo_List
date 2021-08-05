package com.example.clean_todo_list.framework.datasource.network.mappers

import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.util.DateUtil
import com.example.clean_todo_list.business.domain.util.EntityMapper
import com.example.clean_todo_list.framework.datasource.network.model.TaskNetworkEntity

object NetworkMapper : EntityMapper<TaskNetworkEntity, Task>() {

    override fun mapEntityToDomainModel(entity: TaskNetworkEntity): Task = Task(
        id = entity.id,
        title = entity.title,
        body = entity.body,
        isDone = entity.isDone,
        updated_at = DateUtil.convertFirebaseTimestampToUnixTimestamp(entity.updated_at),
        created_at = DateUtil.convertFirebaseTimestampToUnixTimestamp(entity.created_at)
    )

    override fun mapDomainModelToEntity(domainModel: Task): TaskNetworkEntity = TaskNetworkEntity(
        id = domainModel.id,
        title = domainModel.title,
        body = domainModel.body,
        isDone = domainModel.isDone,
        updated_at = DateUtil.convertUnixTimestampToFirebaseTimestamp(domainModel.updated_at),
        created_at = DateUtil.convertUnixTimestampToFirebaseTimestamp(domainModel.created_at)
    )
}