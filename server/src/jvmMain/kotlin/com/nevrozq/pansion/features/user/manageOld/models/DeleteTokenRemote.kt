package com.nevrozq.pansion.features.user.manageOld.models

import kotlinx.serialization.Serializable
import com.nevrozq.pansion.utils.UUIDSerializer
import java.util.UUID

@Serializable
data class DeleteTokenReceive(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID
)