package report

import kotlinx.serialization.Serializable

@Serializable
data class RFetchRecentGradesReceive(
    val login: String
)

@Serializable
data class RFetchRecentGradesResponse(
    val grades: List<Grade>,
    val isAnyDepts: Boolean
)

@Serializable
data class Grade(
    val content: String,
    val reason: String,
    val date: String,
    val reportId: Int?,
    val subjectName: String,
//    val groupName: String
)