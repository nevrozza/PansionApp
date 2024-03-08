package com.nevrozq.pansion.database.groups

data class GroupsDTO(
    val name: String,
    val teacherLogin: String,
    val gSubjectId: Int,
    val difficult: String,
    val isActivated: Boolean
)
