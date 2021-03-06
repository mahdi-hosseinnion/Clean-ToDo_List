package com.example.clean_todo_list.business.data.network

import com.example.clean_todo_list.business.data.network.NetworkErrors.NETWORK_ERROR
import com.example.clean_todo_list.business.domain.state.*


abstract class AuthResponseHandler<ViewState, Data>(
    private val response: ApiResult<Data?>,
    private val stateEvent: StateEvent?
) {

    suspend fun getResult(): DataState<ViewState>? {

        return when (response) {

            is ApiResult.GenericError -> {
                DataState.error(
                    response = Response(
                        message = "${stateEvent?.errorInfo()}\n\nReason: ${response.errorMessage.toString()}",
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Error
                    ),
                    stateEvent = stateEvent
                )
            }

            is ApiResult.NetworkError -> {
                DataState.error(
                    response = Response(
                        message = "${stateEvent?.errorInfo()}\n\nReason: $NETWORK_ERROR",
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Error
                    ),
                    stateEvent = stateEvent
                )
            }

            is ApiResult.Success -> {
                handleSuccess(resultObj = response.value)
            }

        }
    }

    abstract suspend fun handleSuccess(resultObj: Data?): DataState<ViewState>?

}