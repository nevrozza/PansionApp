package admin.groups.subjects.topBar

import kotlinx.serialization.Serializable

@Serializable
data class REditSubjectReceive(
    val subjectId: Int,
    val name: String
)

@Serializable
data class RDeleteSubject(
    val subjectId: Int
)