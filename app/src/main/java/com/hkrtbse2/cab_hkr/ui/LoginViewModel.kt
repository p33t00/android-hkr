package com.hkrtbse2.cab_hkr.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hkrtbse2.cab_hkr.data.UserPreferencesRepository
import com.hkrtbse2.cab_hkr.data.remote.CabApiService
import com.hkrtbse2.cab_hkr.data.remote.dto.ServiceInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginRequestState {
    data class Success(val isLoggedIn: Boolean): LoginRequestState
    object Loading: LoginRequestState
    object Error: LoginRequestState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    val userPreferencesRepo: UserPreferencesRepository,
    val cabApiService: CabApiService
): ViewModel() {
    var isLoggedIn: LoginRequestState by mutableStateOf(LoginRequestState.Loading)
        private set
    var serviceUrl: String by mutableStateOf("")
        private set

    var services: GetServicesReqState by mutableStateOf(GetServicesReqState.Loading)
        private set

    init {
        initLoginStatus()
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

    private fun initLoginStatus() {
        viewModelScope.launch {
            userPreferencesRepo.userServiceUrl.collect {
                serviceUrl = it
            }
        }
        viewModelScope.launch {
            userPreferencesRepo.userLoginState.collect {
                isLoggedIn = LoginRequestState.Success(it)
            }
        }
    }

    private suspend fun  getServices(): List<ServiceInstance> {
        return cabApiService.getServiceInstances()
    }
    fun loginUser() {
        viewModelScope.launch {
            userPreferencesRepo.setUserLoginState(true)
            isLoggedIn = LoginRequestState.Success(true)
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            userPreferencesRepo.removeUserPreferences()
        }
        viewModelScope.launch {
            isLoggedIn = LoginRequestState.Success(false)
            serviceUrl = ""
        }
    }

    fun setUserServiceUrl(url: String) {
        viewModelScope.launch {
            userPreferencesRepo.setUserServiceUrl(url)
            serviceUrl = url
        }
    }
}