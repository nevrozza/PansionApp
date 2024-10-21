package admin.users

import FIO
import kotlinx.serialization.Serializable

@Serializable
data class UserInit(
    @Serializable
    val fio: FIO,
    @Serializable
    val birthday: String,
    @Serializable
    val role: String,
    @Serializable
    val moderation: String,
    @Serializable
    val isParent: Boolean,
)

@Serializable
data class User(
    @Serializable
    val login: String,
    @Serializable
    val isProtected: Boolean,
    @Serializable
    val user: UserInit,
    @Serializable
    val avatarId: Int,
    @Serializable
    val isActive: Boolean,
    val subjectId: Int?
)