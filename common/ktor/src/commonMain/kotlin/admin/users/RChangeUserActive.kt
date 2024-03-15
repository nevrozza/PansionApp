package admin.users

import kotlinx.serialization.Serializable

@Serializable
data class RChangeUserActiveReceive(
    val login: String,
    val isActive: Boolean
)