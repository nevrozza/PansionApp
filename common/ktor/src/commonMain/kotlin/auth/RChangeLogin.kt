package auth

import kotlinx.serialization.Serializable

@Serializable
data class RChangeLogin(
    val newLogin: String
)