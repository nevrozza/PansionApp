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
    val token: String,
    val user: UserInit
)