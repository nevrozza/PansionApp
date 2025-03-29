package com.nevrozq.pansion.database.tokens

import com.nevrozq.pansion.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

data class TokenDTO(
    val deviceId: UUID,
    val login: String,
    val token: UUID,
    val deviceName: String?,
    val deviceType: String,
    val time: String
)

fun TokenDTO.toFetchTokensResponse() = FetchTokensResponseEntity(
    deviceId = this.deviceId,
    deviceName = this.deviceName,
    deviceType = this.deviceType,
    time = this.time
)

@kotlinx.serialization.Serializable
data class FetchTokensResponse(
    val tokens: List<FetchTokensResponseEntity>
)

@kotlinx.serialization.Serializable
data class FetchTokensResponseEntity(
    @Serializable(with = UUIDSerializer::class)
    val deviceId: UUID,
    val deviceName: String?,
    val deviceType: String,
    val time: String
)