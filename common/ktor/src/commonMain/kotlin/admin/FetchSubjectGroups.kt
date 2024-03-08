package admin

import kotlinx.serialization.Serializable



@Serializable
data class FetchSubjectGroupsReceive(
    val id: Int
)


@Serializable
data class FetchSubjectGroupsResponse(
    val groups: List<SubjectGroup>
)

@Serializable
data class SubjectGroup(
    val id: Int,
    val name: String,
    val teacherLogin: String,
    val gSubjectId: Int,
    val difficult: String,
    val isActivated: Boolean
)
