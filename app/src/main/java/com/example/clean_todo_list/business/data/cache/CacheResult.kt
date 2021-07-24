package com.example.clean_todo_list.business.data.cache

sealed class CacheResult<out T> {

    data class Success<out T>(val value: T) : CacheResult<T>()

    data class GenericError(
        val error: String
    ) : CacheResult<Nothing>()
}