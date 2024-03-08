package admin

import kotlinx.serialization.Serializable

@Serializable
data class EditUserReceive(
    val login: String,
    val name: String,
    val surname: String,
    val praname: String?,
    val birthday: String?,
    val role: String,
    val moderation: String,
    val isParent: Boolean
)


@Serializable
data class EditUserResponse(
    val isGood: Boolean
)