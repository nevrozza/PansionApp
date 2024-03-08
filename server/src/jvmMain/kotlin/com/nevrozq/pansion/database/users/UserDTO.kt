package com.nevrozq.pansion.database.users

import com.nevrozq.pansion.database.users.Users.nullable

data class UserDTO(
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

