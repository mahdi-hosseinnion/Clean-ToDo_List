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

    fun createListOfTask(count: Int): List<Task> {
        val result = ArrayList<Task>()
        for (i in 0 until count) {
            //random true or false for empty or full body
            val coin = Random.nextBoolean()
            //random true or false for done or ongoing task
            val coin1 = Random.nextBoolean()
            result.add(
                createTask(
                    id = UUID.randomUUID().toString(),
                    title = UUID.randomUUID().toString(),
                    body = if (coin) UUID.randomUUID().toString() else null,
                    isDone = coin1
                )
            )
        }
        return result
    }

    fun createRandomTask(): Task = createTask(
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        Random.nextBoolean()
    )
}