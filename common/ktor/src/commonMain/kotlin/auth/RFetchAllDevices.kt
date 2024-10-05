package auth

import kotlinx.serialization.Serializable

@Serializable
data class RFetchAllDevicesResponse(
    val tokens: List<Device>,
    val secondLogin: String?
)

@Serializable
data class Device(
    val deviceId: String,
    val deviceName: String?,
    val deviceType: String,
    val time: String,
    val isThisSession: Boolean
)