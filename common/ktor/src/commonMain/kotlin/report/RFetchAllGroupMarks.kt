package report

import kotlinx.serialization.Serializable

@Serializable
data class RFetchAllGroupMarksReceive(
    val subjectId: Int,
    val groupId: Int
)

@Serializable
data class RFetchAllGroupMarksResponse(
    val students: List<AllGroupMarksStudent>
)

@Serializable
data class AllGroupMarksStudent(
    val login: String,
    val shortFIO: String,
    val isQuarters: Boolean,
    val marks: List<UserMarkPlus>,
    val stups: List<UserMarkPlus>
)