package admin.groups.subjects

import TeacherPerson
import kotlinx.serialization.Serializable

@Serializable
data class RFetchTeachersResponse(
    val teachers: List<TeacherPerson>
)

