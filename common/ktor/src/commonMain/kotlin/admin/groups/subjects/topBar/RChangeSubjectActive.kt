package admin.groups.subjects.topBar

import kotlinx.serialization.Serializable

@Serializable
data class RChangeSubjectActiveReceive(
    val subjectId: String,
    val isActive: Boolean
)