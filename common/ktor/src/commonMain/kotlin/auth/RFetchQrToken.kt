package auth

import kotlinx.serialization.Serializable

@Serializable
data class RFetchQrTokenResponse(
    val token: String
)
@Serializable
data class RFetchQrTokenReceive(
    val deviceId: String,
    val deviceType: String,
    val deviceName: String,
)

@Serializable
data class RActivateQrTokenResponse(
    val deviceName: String,
    val deviceType: String
)

