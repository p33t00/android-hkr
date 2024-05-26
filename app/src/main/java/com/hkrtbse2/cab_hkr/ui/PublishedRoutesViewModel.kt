package com.hkrtbse2.cab_hkr.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hkrtbse2.cab_hkr.data.remote.CabApiService
import com.hkrtbse2.cab_hkr.data.remote.dto.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GetPubMessagesReqState {
    data class Success(val data: List<Message>): GetPubMessagesReqState
    object Error : GetPubMessagesReqState
    object Loading : GetPubMessagesReqState
}

@HiltViewModel
class PublishedRoutesViewModel @Inject constructor(val cabApiService: CabApiService): ViewModel() {
    var routePlanMessagesState: GetPubMessagesReqState by mutableStateOf(GetPubMessagesReqState.Loading)
        private set

    init {
        initRPMessages()
    }

    fun initRPMessages() {
        routePlanMessagesState = GetPubMessagesReqState.Loading
        viewModelScope.launch {
            try {
                routePlanMessagesState = GetPubMessagesReqState.Success(cabApiService.getPublishedMessages())
            } catch (e: Exception) {
                routePlanMessagesState = GetPubMessagesReqState.Error
                e.printStackTrace()
            }
        }
    }

    fun getPublishedMessage(id: String): Message? {
        return if (routePlanMessagesState is GetPubMessagesReqState.Success) {
            return (routePlanMessagesState as GetPubMessagesReqState.Success).data.find { m -> m.messageId == id }
        } else {
            null
        }
    }
}