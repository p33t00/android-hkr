package com.hkrtbse2.cab_hkr.data.remote.dto

@Serializable
data class ServiceInstance(
    val id: Int,
    val name: String,
    val version: String,
    val comment: String,
    val gsaaa: String?,
    val instanceId: String,
    val keywords: String,
    val status: String,
    val urlcode: String,
    val endpointUri: String,
    val endpointType: String,
    val mbi: String?,
    val abc: String?,
    val serviceType: String,
    val gdId: String,
    val someId: String?,
    val compliant: String,
    val publishedAt: String,
    val lastUpdatedAt: String?,
    val versionIdPs: String?,
    val doc: String?,
)
