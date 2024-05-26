package com.hkrtbse2.cab_hkr.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hkrtbse2.cab_hkr.data.remote.CabApiService
import com.hkrtbse2.cab_hkr.data.remote.dto.AuthorizedIdentity
import com.hkrtbse2.cab_hkr.data.remote.dto.Organization
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface IdentitiesReqState {
    object InitIdentitiesSuccess : IdentitiesReqState
    object SaveAclSuccess : IdentitiesReqState
    object InitIdentitiesError : IdentitiesReqState
    object SaveAclError : IdentitiesReqState
    object Loading : IdentitiesReqState
}

@HiltViewModel
class IdentitiesViewModel @Inject constructor(
    private val cabApiService: CabApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val dataId: String = savedStateHandle.get<String>("dataId").orEmpty()
    private var authorizedIdentities: List<AuthorizedIdentity> = emptyList()
    var reqStatus: IdentitiesReqState by mutableStateOf(IdentitiesReqState.Loading)
        private set
    var identities = mutableStateListOf<Organization>()
        private set

    init {
        initRoutePlanAcl()
    }

    fun initRoutePlanAcl() {
        reqStatus = IdentitiesReqState.Loading
        viewModelScope.launch {
            try {
                initAuthorizedIdentities()
                val identitiesWithState = setIdentitiesState(getOrganizations(), authorizedIdentities)
                identities.addAll(identitiesWithState)
                reqStatus = IdentitiesReqState.InitIdentitiesSuccess
            } catch (e: Exception) {
                reqStatus = IdentitiesReqState.InitIdentitiesError
                e.printStackTrace()
            }
        }
    }

    private suspend fun getOrganizations() = cabApiService.getOrganizations().organizations

    private suspend fun initAuthorizedIdentities() {
        authorizedIdentities = cabApiService.getAuthorizedIdentities(dataId)
    }

    fun changeCheckState(idx: Int) {
        identities[idx] = identities[idx].copy(checked = !identities[idx].checked)
    }

    private fun setIdentitiesState(
        identities: List<Organization>,
        identitiesWithState: List<AuthorizedIdentity>
    ): List<Organization> {
        val processed = mutableListOf<Organization>()
        for (org in identities) {
            if (identitiesWithState.find { i -> org.mgip == i.identityId } != null) {
                processed.add(org.copy(checked = true))
            } else {
                processed.add(org)
            }
        }
        return processed
    }

    fun saveRoutePlanAcl() {
        viewModelScope.launch {
            try {
                reqStatus = IdentitiesReqState.Loading
                val checked = identities.filter { it.checked }.apply {
                    if (this.isNotEmpty()) postRoutePlanIdentities(mapToAuthIdentity(this))
                }

                authorizedIdentities.filter { i -> checked.find { it.mgip == i.identityId } == null }
                    .apply { if (this.isNotEmpty()) deleteRoutePlanIdentities(this) }

                authorizedIdentities = mapToAuthIdentity(checked)
                reqStatus = IdentitiesReqState.SaveAclSuccess
            } catch (e: Exception) {
                reqStatus = IdentitiesReqState.SaveAclError
                e.printStackTrace()
            }
        }
    }

    private fun mapToAuthIdentity(identities: List<Organization>): List<AuthorizedIdentity> {
        return identities.map { i -> AuthorizedIdentity(i.mgip, i.name) }
    }

    private suspend fun postRoutePlanIdentities(identities: List<AuthorizedIdentity>) {
        cabApiService.postAuthorizedIdentities(dataId, identities)
    }

    private suspend fun deleteRoutePlanIdentities(identities: List<AuthorizedIdentity>) {
        cabApiService.deleteAuthorizedIdentities(dataId, identities)
    }
}