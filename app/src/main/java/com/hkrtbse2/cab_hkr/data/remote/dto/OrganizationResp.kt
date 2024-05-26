package com.hkrtbse2.cab_hkr.data.remote.dto


@Serializable
data class OrganizationResp(
    val organizations: List<Organization>,
    val statusCode: Int,
    val msg: String?
)
