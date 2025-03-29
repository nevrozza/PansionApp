package com.nevrozq.pansion.utils

import java.util.UUID

enum class UserRoles {
    Admin, Mvd, Student
}

val nullUUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")