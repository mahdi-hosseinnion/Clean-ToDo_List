package com.example.clean_todo_list.framework.datasource.network.task.implemetation

import com.example.clean_todo_list.business.domain.model.Task
import com.example.clean_todo_list.business.domain.util.DateUtil
import com.example.clean_todo_list.framework.datasource.network.task.abstraction.TaskFirestoreService
import com.example.clean_todo_list.framework.datasource.network.mappers.NetworkMapper
import com.example.clean_todo_list.framework.datasource.network.model.TaskNetworkEntity
import com.example.clean_todo_list.util.cLog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore doc refs:
 * 1. add:  https://firebase.google.com/docs/firestore/manage-data/add-data
 * 2. delete: https://firebase.google.com/docs/firestore/manage-data/delete-data
 * 3. update: https://firebase.google.com/docs/firestore/manage-data/add-data#update-data
 * 4. query: https://firebase.google.com/docs/firestore/query-data/queries
 */

@Singleton
class TaskFirestoreServiceImpl
@Inject
constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : TaskFirestoreService {

    private fun userUId(): String =
        auth.currentUser?.uid ?: throw Throwable(message = "User value is null")

    override suspend fun insertTask(task: Task) {
        val entity = NetworkMapper.mapDomainModelToEntity(task)
        firestore
            .collection(TASKS_COLLECTION)
            .document(userUId())
            .collection(TASKS_COLLECTION)
            .document(entity.id)
            .set(entity)
            .addOnFailureListener {
                cLog(it.message, "insertOrUpdateTask")
            }
            .await()
    }

    override suspend fun insertTasks(tasks: List<Task>) {
        if (tasks.size > 500) {
            throw Exception("Cannot insert more than 500 notes at a time into firestore.")
        }

        val collectionRef = firestore
            .collection(TASKS_COLLECTION)
            .document(userUId())
            .collection(TASKS_COLLECTION)

        firestore.runBatch { batch ->
            for (task in tasks) {
                val entity = NetworkMapper.mapDomainModelToEntity(task)
                val documentRef = collectionRef.document(task.id)
                batch.set(documentRef, entity)
            }
        }.addOnFailureListener {
            cLog(it.message, "insertOrUpdateTasks")
        }.await()
    }

    override suspend fun updateTask(task: Task, updated_at: Long) {
        val entity = NetworkMapper.mapDomainModelToEntity(task)
        entity.updated_at = DateUtil.convertUnixTimestampToFirebaseTimestamp(updated_at)
        firestore
            .collection(TASKS_COLLECTION)
            .document(userUId())
            .collection(TASKS_COLLECTION)
            .document(entity.id)
            .set(entity)
            .addOnFailureListener {
                cLog(it.message, "insertOrUpdateTask")
            }
            .await()
    }

    override suspend fun deleteTask(primaryKey: String) {

        firestore
            .collection(TASKS_COLLECTION)
            .document(userUId())
            .collection(TASKS_COLLECTION)
            .document(primaryKey)
            .delete()
            .addOnFailureListener {
                cLog(it.message, "deleteTask")
            }
            .await()
    }

    override suspend fun insertDeletedTask(task: Task) {
        val entity = NetworkMapper.mapDomainModelToEntity(task)
        firestore
            .collection(DELETES_COLLECTION)
            .document(userUId())
            .collection(TASKS_COLLECTION)
            .document(entity.id)
            .set(entity)
            .addOnFailureListener {
                cLog(it.message, "insertDeletedTask")
            }
            .await()
    }

    override suspend fun insertDeletedTasks(tasks: List<Task>) {
        if (tasks.size > 500) {
            throw Exception("Cannot add more than 500 tasks at a time in firestore.")
        }
        val collectionRef = firestore
            .collection(DELETES_COLLECTION)
            .document(userUId())
            .collection(TASKS_COLLECTION)
        firestore.runBatch { batch ->
            for (task in tasks) {
                val documentRef = collectionRef.document(task.id)
                batch.set(documentRef, NetworkMapper.mapDomainModelToEntity(task))
            }
        }.addOnFailureListener {
            cLog(it.message, "insertDeletedTasks")
        }
            .await()
    }

    override suspend fun deleteDeletedTask(task: Task) {
        val entity = NetworkMapper.mapDomainModelToEntity(task)
        firestore
            .collection(DELETES_COLLECTION)
            .document(userUId())
            .collection(TASKS_COLLECTION)
            .document(entity.id)
            .delete()
            .addOnFailureListener {
                cLog(it.message, "deleteDeletedTask")
            }
            .await()
    }

    override suspend fun deleteDeletedTasks(tasks: List<Task>) {
        if (tasks.size > 500) {
            throw Exception("Cannot delete more than 500 tasks at a time in firestore.")
        }
        val collectionRef = firestore
            .collection(DELETES_COLLECTION)
            .document(userUId())
            .collection(TASKS_COLLECTION)
        firestore.runBatch { batch ->
            for (task in tasks) {
                val documentRef = collectionRef.document(task.id)
                batch.delete(documentRef)
            }
        }.addOnFailureListener {
            cLog(it.message, "deleteDeletedTasks")
        }.await()
    }

    override suspend fun getDeletedTasks(): List<Task> {
        return NetworkMapper.mapEntityListToDomainModelList(
            firestore
                .collection(DELETES_COLLECTION)
                .document(userUId())
                .collection(TASKS_COLLECTION)
                .get()
                .addOnFailureListener {
                    cLog(it.message, "getDeletedTasks")
                }
                .await()
                .toObjects(TaskNetworkEntity::class.java)
        )
    }

    // used in testing
    override suspend fun deleteAllTasks() {
        firestore
            .collection(TASKS_COLLECTION)
            .document(userUId())
            .delete()
            .addOnFailureListener {
                cLog(it.message, "deleteAllTasks1")
            }
            .await()
        firestore
            .collection(DELETES_COLLECTION)
            .document(userUId())
            .delete().addOnFailureListener {
                cLog(it.message, "deleteAllTasks2")
            }
            .await()
    }

    override suspend fun searchTask(task: Task): Task? {
        return firestore
            .collection(TASKS_COLLECTION)
            .document(userUId())
            .collection(TASKS_COLLECTION)
            .document(task.id)
            .get()
            .addOnFailureListener {
                cLog(it.message, "searchTask")
            }
            .await()
            .toObject(TaskNetworkEntity::class.java)?.let {
                NetworkMapper.mapEntityToDomainModel(it)
            }
    }

    override suspend fun getAllTasks(): List<Task> {
        return NetworkMapper.mapEntityListToDomainModelList(
            firestore
                .collection(TASKS_COLLECTION)
                .document(userUId())
                .collection(TASKS_COLLECTION)
                .get()
                .addOnFailureListener {
                    cLog(it.message, "getAllTasks")
                }
                .await()
                .toObjects(TaskNetworkEntity::class.java)
        )
    }


    override suspend fun updateIsDone(taskId: String, isDone: Boolean) {
        firestore
            .collection(TASKS_COLLECTION)
            .document(userUId())
            .collection(TASKS_COLLECTION)
            .document(taskId)
            .update(
                "isDone", isDone,
                "updated_at", Timestamp.now()
            )
            .addOnFailureListener {
                cLog(it.message, "updateIsDone")
            }
            .await()
    }

    companion object {
        const val TASKS_COLLECTION = "tasks"
        const val DELETES_COLLECTION = "deletes"
//        const val USER_ID = "aEqm03OkhqWJUE81yFLlHXCxkdM2" // hardcoded for single user
    }
}