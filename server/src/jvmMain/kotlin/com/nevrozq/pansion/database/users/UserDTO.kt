package com.nevrozq.pansion.database.users

import FIO
import admin.users.User
import admin.users.UserInit
import com.nevrozq.pansion.database.users.Users.nullable
import kotlinx.serialization.Serializable

data class UserDTO(
    val login: String,
    val password: String?,
    val name: String,
    val surname: String,
    val praname: String?,
    val birthday: String,
    val role: String,
    val moderation: String,
    val isParent: Boolean,
    val avatarId: Int,
    val isActive: Boolean,
    val subjectId: Int?
)

fun UserDTO.mapToUser() =
    User(
        login = this.login,
        isProtected = this.password != null,
        user = UserInit(
            fio = FIO(
                name = this.name,
                surname = this.surname,
                praname = this.praname,
            ),
            birthday = this.birthday,
            role = this.role,
            moderation = this.moderation,
            isParent = this.isParent
        ),
        avatarId = this.avatarId,
        isActive = this.isActive,
        subjectId = this.subjectId
    )
