package admin.users

import kotlinx.serialization.Serializable

@Serializable
data class RDeleteUserReceive(
    val login: String,
    val user: UserInit
)