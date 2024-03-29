package auth


import FIO
import kotlinx.serialization.Serializable

@Serializable
data class LoginReceive(
    val login: String,
    val password: String,
    val deviceId: String,
    val deviceName: String?,
    val deviceType: String
)

@Serializable
data class LoginResponse(
    val activation: ActivationResponse,
    val avatarId: Int
)