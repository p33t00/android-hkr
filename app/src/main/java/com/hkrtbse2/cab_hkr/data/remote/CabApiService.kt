package com.hkrtbse2.cab_hkr.data.remote

import com.hkrtbse2.cab_hkr.data.UserPreferencesRepository
import com.hkrtbse2.cab_hkr.data.remote.dto.AuthorizedIdentity
import com.hkrtbse2.cab_hkr.data.remote.dto.Message
import com.hkrtbse2.cab_hkr.data.remote.dto.OrganizationResp
import com.hkrtbse2.cab_hkr.data.remote.dto.ServiceInstance
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CabApiService(
    private val coroutineScope: CoroutineScope,
    private val userPrefRepo: UserPreferencesRepository,
    private val client: HttpClient
) {
    var svcUrl = ""
    init {
        coroutineScope.launch { initServiceUrl() }
    }
    suspend fun initServiceUrl() {
        userPrefRepo.userServiceUrl.collect { svcUrl = it.replace("hkr", "hkr-conf") }
    }

    suspend fun getOrganizations(): OrganizationResp {
        return client.get("$svcUrl/findId") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun getPublishedMessages(): List<Message> {
        return client.get("$svcUrl/getMsgOnline") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }.body()
    }

    @OptIn(InternalAPI::class)
    suspend fun postMessage(dataId: String, message: String, type: String = "MXP"): HttpResponse {
        return client.post("$svcUrl/publishAlarm") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            parameter("dataId", dataId)
            parameter("msgType", type)
            body = Json.encodeToString(message)
        }
    }

    suspend fun getAuthorizedIdentities(dataId: String): List<AuthorizedIdentity> {
        return client.get("$svcUrl/authId") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            parameter("dataId", dataId)
        }.body()
    }


    @OptIn(InternalAPI::class)
    suspend fun postAuthorizedIdentities(
        dataId: String,
        identities: List<AuthorizedIdentity>
    ): HttpResponse {
        return client.post("$svcUrl/authId") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            parameter("dataId", dataId)
            body = Json.encodeToString(identities)

        }
    }

    @OptIn(InternalAPI::class)
    suspend fun deleteAuthorizedIdentities(
        dataId: String,
        identities: List<AuthorizedIdentity>
    ): HttpResponse {
        return client.delete("$svcUrl/authId") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            parameter("dataId", dataId)
            body = Json.encodeToString(identities)

        }
    }

    suspend fun getServiceInstances(): List<ServiceInstance> {
        return client.get("${HttpRoutes.SERVICE_REGISTRY_API_URL.url}/svc?page=0&size=2000") {
            accept(ContentType.Application.Json)
        }.body()
    }
}