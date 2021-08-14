package com.example.clean_todo_list.framework.datasource.data

import android.app.Application
import android.content.res.AssetManager
import com.example.clean_todo_list.business.domain.model.Task
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskDataFactory
@Inject
constructor(
    private val application: Application
) {

    fun produceListOfTasks(assetName: String = "task_list.json"): List<Task> {
        return Gson()
            .fromJson(
                convertJsonFromAssetsToString(assetName),
                object : TypeToken<List<Task>>() {}.type
            )
    }

    fun produceEmptyListOfTasks(): List<Task> = emptyList()

    private fun convertJsonFromAssetsToString(assertName: String): String? {
        return try {
            val inputStream: InputStream = (application.assets as AssetManager).open(assertName)
            inputStream.bufferedReader().use { it.readText() }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }
}