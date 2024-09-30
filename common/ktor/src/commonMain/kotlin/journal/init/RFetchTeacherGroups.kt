package journal.init

import admin.groups.forms.CutedGroup
import kotlinx.serialization.Serializable

@Serializable
data class RFetchTeacherGroupsResponse(
    val groups: List<TeacherGroup>
)

@Serializable
data class TeacherGroup(
    val cutedGroup: CutedGroup,
    val subjectId: Int,
    val subjectName: String,
    val teacherLogin: String
)