package auth

import kotlinx.serialization.Serializable

@Serializable
data class RChangeAvatarIdReceive(
    val avatarId: Int,
    val price: Int
)