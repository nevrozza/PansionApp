package admin

import kotlinx.serialization.Serializable

@Serializable
data class ClearUserPasswordReceive(
    val login: String
)


@Serializable
data class ClearUserPasswordResponse(
    val isGood: Boolean
)