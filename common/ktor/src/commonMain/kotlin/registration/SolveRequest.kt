package registration

import kotlinx.serialization.Serializable

@Serializable
data class SolveRequestReceive(
    val isAccepted: Boolean,
    val request: RegistrationRequest
)