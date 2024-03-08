package auth


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
    val token: String,
    val name: String,
    val surname: String,
    val praname: String?,
    val role: String,
    val moderation: String
)