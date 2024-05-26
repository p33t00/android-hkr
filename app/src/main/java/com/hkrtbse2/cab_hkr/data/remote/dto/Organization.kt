package com.hkrtbse2.cab_hkr.data.remote.dto

@Serializable
data class Organization(
    val country: String,
    val email: String,
    val mgip: String,
    val name: String,
    val checked: Boolean = false
)
