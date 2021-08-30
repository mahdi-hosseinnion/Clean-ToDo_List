package com.example.clean_todo_list.business.data.util

import com.example.clean_todo_list.business.data.cache.CacheConstants.CACHE_TIMEOUT
import com.example.clean_todo_list.business.data.cache.CacheErrors.CACHE_ERROR_TIMEOUT
import com.example.clean_todo_list.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.example.clean_todo_list.business.data.cache.CacheResult
import com.example.clean_todo_list.business.data.network.ApiResult
import com.example.clean_todo_list.business.data.network.NetworkConstants.NETWORK_TIMEOUT
import com.example.clean_todo_list.business.data.network.NetworkErrors.NETWORK_ERROR_NULL
import com.example.clean_todo_list.business.data.network.NetworkErrors.NETWORK_ERROR_TIMEOUT
import com.example.clean_todo_list.business.data.network.NetworkErrors.NETWORK_ERROR_UNKNOWN
import com.example.clean_todo_list.business.data.util.GenericErrors.ERROR_UNKNOWN
import com.example.clean_todo_list.util.cLog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException

/**
 * Reference: https://medium.com/@douglas.iacovelli/how-to-handle-errors-with-retrofit-and-coroutines-33e7492a912
 */

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T?
): ApiResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(NETWORK_TIMEOUT) {
                ApiResult.Success(apiCall.invoke())
            }
        } catch (throwable: Throwable) {
            cLog(throwable.message, "safeApiCall")
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = 408 // timeout error code
                    ApiResult.GenericError(code, NETWORK_ERROR_TIMEOUT)
                }
                is IOException -> {
                    ApiResult.NetworkError
                }
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ApiResult.GenericError(
                        code,
                        errorResponse
                    )
                }
                else -> {
                    ApiResult.GenericError(
                        null,
                        NETWORK_ERROR_UNKNOWN
                    )
                }
            }
        }
    }
}

suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T?
): CacheResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(CACHE_TIMEOUT) {
                CacheResult.Success(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            cLog(throwable.message, "safeCacheCall")
            throwable.printStackTrace()
            when (throwable) {

                is TimeoutCancellationException -> {
                    CacheResult.GenericError(CACHE_ERROR_TIMEOUT)
                }
                else -> {
                    CacheResult.GenericError(CACHE_ERROR_UNKNOWN)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ERROR_UNKNOWN
    }
}

suspend fun <T> safeAuthCall(
    dispatcher: CoroutineDispatcher,
    authCall: suspend () -> Task<T>?
): ApiResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(NETWORK_TIMEOUT) {
                val result = authCall.invoke()

                if (result != null) {

                    if (result.isSuccessful) {
                        ApiResult.Success(result.result)
                    } else {
                        cLog(result.exception?.message, "safeAuthCall 1 ")
                        ApiResult.GenericError(errorMessage = result.exception?.message)
                    }

                } else {
                    //result is null
                    cLog("auth call return is null", "safeAuthCall 2 ")
                    ApiResult.GenericError(errorMessage = NETWORK_ERROR_NULL)
                }
            }
        } catch (throwable: Throwable) {
            cLog(throwable.message, "safeAuthCall 3 ")
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = 408 // timeout error code
                    ApiResult.GenericError(code, NETWORK_ERROR_TIMEOUT)
                }
                is IOException -> {
                    ApiResult.NetworkError
                }
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ApiResult.GenericError(
                        code,
                        errorResponse
                    )
                }
                //TODO FIX THIS WHY THIS CALLED? INSTEAD OF ELSE OF IS SUCCESSFUL?
                is FirebaseAuthException ->{
                    ApiResult.GenericError(
                        null,
                        throwable.message
                    )
                }
                else -> {
                    ApiResult.GenericError(
                        null,
                        NETWORK_ERROR_UNKNOWN
                    )
                }
            }
        }
    }
}
