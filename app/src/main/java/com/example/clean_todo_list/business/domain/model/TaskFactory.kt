package com.example.clean_todo_list.business.domain.model

import com.example.clean_todo_list.business.domain.util.DateUtil
import java.util.*
import kotlin.random.Random

object TaskFactory {

    fun createTask(
        id: String? = null,
        title: String,
        body: String? = null,
        isDone: Boolean? = null
    ): Task = Task(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        isDone = isDone ?: false,
        updated_at = DateUtil.getCurrentTimestamp(),
        created_at = DateUtil.getCurrentTimestamp()
    )

    fun createListOfRandomTasks(count: Int): List<Task> {
        val result = ArrayList<Task>()
        repeat(count) {

            result.add(
                createRandomTask()
            )
        }
        return result
    }

    fun createRandomTask(): Task {
        val now = DateUtil.getCurrentTimestamp()
        val range = 100_000
        val coin1 = Random.nextBoolean()
        val coin2 = Random.nextBoolean()

        val created_at = Random.nextLong(now.minus(range), now.plus(range))
        val updated_at =
            if (coin1)
                created_at//task did not updated
            else
                Random.nextLong(created_at, created_at.plus(range))//task have been updated

        if (created_at > updated_at) {
            throw Exception("created_at should not be greater then updated_at value")
        }
        return Task(
            id = UUID.randomUUID().toString(),
            title = UUID.randomUUID().toString(),
            body = if (coin2) UUID.randomUUID().toString() else "",//empty or not empty body
            isDone = Random.nextBoolean(),
            updated_at = updated_at,
            created_at = created_at
        )
    }
}