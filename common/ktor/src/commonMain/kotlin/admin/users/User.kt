package admin.users

import FIO
import kotlinx.serialization.Serializable

@Serializable
data class UserInit(
    val fio: FIO,
    val birthday: String,
    val role: String,
    val moderation: String,
    val isParent: Boolean,
)

@Serializable
data class User(
    val login: String,
    val isProtected: Boolean,
    val user: UserInit,
    val avatarId: Int,
    val isActive: Boolean
)