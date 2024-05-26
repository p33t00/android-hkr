package com.hkrtbse2.cab_hkr.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hkrtbse2.cab_hkr.data.remote.CabApiService
import com.hkrtbse2.cab_hkr.data.remote.dto.ServiceInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GetServicesReqState {
    data class Success(val data: List<ServiceInstance>): GetServicesReqState
    object Error : GetServicesReqState
    object Loading : GetServicesReqState
}
@HiltViewModel
class ServicesViewModel @Inject constructor(
    val cabApiService: CabApiService
): ViewModel() {
    var services: GetServicesReqState by mutableStateOf(GetServicesReqState.Loading)
        private set

    init {
        initServices()
    }

    fun initServices() {
        try {
            services = GetServicesReqState.Loading
            viewModelScope.launch {
                services = GetServicesReqState.Success(getServices())
            }
        } catch (e: Exception) {
            services = GetServicesReqState.Error
        }
    }

    private suspend fun  getServices(): List<ServiceInstance> {
        return cabApiService.getServiceInstances()
    }
}