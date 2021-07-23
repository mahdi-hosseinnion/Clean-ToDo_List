package com.example.clean_todo_list.business.domain.model

import com.example.clean_todo_list.business.domain.util.DateUtil
import java.util.*
import kotlin.random.Random

object TodoFactory {

    fun createTodo(
        id: String?,
        title: String,
        body: String?,
        isDone: Boolean?
    ): Todo = Todo(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        isDone = isDone ?: false,
        updated_at = DateUtil.getCurrentTimestamp(),
        created_at = DateUtil.getCurrentTimestamp()
    )

    fun createListOfTodo(count: Int): List<Todo> {
        val result = ArrayList<Todo>()
        for (i in 0 until count) {
            //random true or false for empty or full body
            val coin = Random.nextBoolean()
            //random true or false for done or ongoing task
            val coin1 = Random.nextBoolean()
            result.add(
                createTodo(
                    id = null,
                    title = UUID.randomUUID().toString(),
                    body = if (coin) UUID.randomUUID().toString() else null,
                    isDone = coin1
                )
            )
        }
        return result
    }
}