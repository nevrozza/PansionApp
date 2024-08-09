package auth

import kotlinx.serialization.Serializable

@Serializable
data class RTerminateDeviceReceive(
    val id: String
)