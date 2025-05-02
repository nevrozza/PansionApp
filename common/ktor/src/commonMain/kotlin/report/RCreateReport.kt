package report

import kotlinx.serialization.Serializable

@Serializable
data class RCreateReportReceive(
    val groupId: Int,
    val date: String,
    val time: String,
    val studentLogins: List<String>,
    val deletedStudentLogins: List<String>,
    val lessonId: Int?
)

@Serializable
data class RCreateReportResponse(
    val reportId: Int
)