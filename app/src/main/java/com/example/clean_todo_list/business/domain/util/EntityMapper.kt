package com.example.clean_todo_list.business.domain.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class EntityMapper<Entity, DomainModel> {

    abstract fun mapEntityToDomainModel(entity: Entity): DomainModel

    abstract fun mapDomainModelToEntity(domainModel: DomainModel): Entity

    fun mapEntityListToDomainModelList(entityList: List<Entity>): List<DomainModel> =
        entityList.map {
            mapEntityToDomainModel(it)
        }

    fun mapEntityFlowToDomainModelFlow(entityList: Flow<List<Entity>>): Flow<List<DomainModel>> =
        entityList.map {
            mapEntityListToDomainModelList(it)
        }

    fun mapDomainModelListToEntityList(domainList: List<DomainModel>): List<Entity> =
        domainList.map {
            mapDomainModelToEntity(it)
        }

}