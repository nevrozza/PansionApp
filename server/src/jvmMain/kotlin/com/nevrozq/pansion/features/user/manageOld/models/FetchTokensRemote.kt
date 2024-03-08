package com.nevrozq.pansion.features.user.manageOld.models

import kotlinx.serialization.Serializable
import com.nevrozq.pansion.utils.UUIDSerializer
import java.util.UUID

@Serializable
data class FetchTokensResponse(
    val tokens: List<FetchTokensResponseEntity>
)

@Serializable
data class FetchTokensResponseEntity(
    @Serializable(with = UUIDSerializer::class)
    val deviceId: UUID,
    val deviceName: String?,
    val deviceType: String,
    val time: String
)
