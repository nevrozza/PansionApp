package registration

import kotlinx.serialization.Serializable

@Serializable
data class SendRegistrationRequestReceive(
    val request: RegistrationRequest,
    val deviceId: String
)