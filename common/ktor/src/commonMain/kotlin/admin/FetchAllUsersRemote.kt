package admin

import kotlinx.serialization.Serializable



@Serializable
data class FetchAllUsersResponse(
    val users: List<User>
)

@Serializable
data class User(
    val login: String,
    val password: String?,
    val name: String,
    val surname: String,
    val praname: String?,
    val birthday: String?,
    val role: String,
    val moderation: String,
    val isParent: Boolean,
    val avatarId: Int
)

