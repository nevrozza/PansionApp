package admin

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceive(
    val name: String,
    val surname: String,
    val praname: String?,
    val birthday: String?,
//    val lessons: String?,
    val role: String,
    val moderation: String,
    val isParent: Boolean
)


@Serializable
data class RegisterResponse(
    val login: String
)

@Serializable
data class UserForRegistration(
    val name: String,
    val surname: String,
    val praname: String?,
    val birthday: String?,
    val role: String,
    val moderation: String,
    val isParent: Boolean,
)