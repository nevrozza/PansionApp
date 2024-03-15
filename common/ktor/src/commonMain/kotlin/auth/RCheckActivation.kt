package auth


import kotlinx.serialization.Serializable

@Serializable
data class CheckActivationReceive(
    val login: String
)

@Serializable
data class CheckActivationResponse(
    val name: String?,
    val isActivated: Boolean
)