package auth

import FIO
import admin.users.UserInit
import kotlinx.serialization.Serializable

@Serializable
data class ActivationReceive(
    val login: String,
    val password: String,
    val deviceId: String,
    val deviceName: String?,
    val deviceType: String
)

@Serializable
data class ActivationResponse(
    @Serializable
    val token: String,
    @Serializable
    val login: String,
    @Serializable
    val user: UserInit
)