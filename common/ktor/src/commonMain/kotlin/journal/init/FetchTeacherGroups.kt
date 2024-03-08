package journal.init

import admin.SubjectGroup
import kotlinx.serialization.Serializable

@Serializable
data class FetchTeacherGroupsResponse(
    val groups: List<TeacherGroup>
)

@Serializable
data class TeacherGroup(
    val id: Int,
    val name: String,
    val subjectNum: Int,
    val isActivated: Boolean
)