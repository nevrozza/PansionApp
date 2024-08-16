package auth

import kotlinx.serialization.Serializable
//token = r.activation.token,
//            name = r.activation.user.fio.name,
//            surname = r.activation.user.fio.surname,
//            praname = r.activation.user.fio.praname,
//            role = r.activation.user.role,
//            moderation = r.activation.user.moderation,
//            login = r.activation.login,
//            avatarId = r.avatarId
@Serializable
data class RCheckConnectionResponse(
    val isTokenValid: Boolean,
    val name: String,
    val surname: String,
    val praname: String?,
    val role: String,
    val moderation: String,
    val avatarId: Int,
    val isParent: Boolean,
    val birthday: String,
    val version: Int
)