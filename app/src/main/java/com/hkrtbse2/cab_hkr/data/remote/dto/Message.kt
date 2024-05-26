package com.hkrtbse2.cab_hkr.data.remote.dto

import kotlinx.serialization.SerialName

@Serializable
class Message(
    @SerialName("Message")
    val message: String,
    @SerialName("MessageID")
    val messageId: String,
    @SerialName("id[")
    val idp: String?,
    @SerialName("std")
    val std: Int,
    @SerialName("MessageType")
    val messageType: String, // "RTZ"
    @SerialName("valCaa")
    val valCaa: String?,
    @SerialName("goToId")
    val goToId: String?,
    @SerialName("PublishTime")
    val publishTime: String?
) {
    val getMsgIdMetadata = messageId.substringBeforeLast(':')
    val getShortMsgId = messageId.substringAfterLast(':')
}

