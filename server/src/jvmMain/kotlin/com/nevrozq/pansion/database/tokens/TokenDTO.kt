package com.nevrozq.pansion.database.tokens

import com.nevrozq.pansion.features.user.manageOld.models.FetchTokensResponseEntity
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
