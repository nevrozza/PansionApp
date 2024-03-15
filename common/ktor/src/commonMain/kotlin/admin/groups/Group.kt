package admin.groups

import kotlinx.serialization.Serializable

@Serializable
data class GroupInit(
    val name: String,
    val teacherLogin: String,
    val subjectId: Int,
    val difficult: String
)

@Serializable
data class Group(
    val id: Int,
    val group: GroupInit,
    val isActive: Boolean
)


