package report

import kotlinx.serialization.Serializable

@Serializable
data class RFetchHeadersResponse(
    val reportHeaders: List<ReportHeader>,
    val currentModule: String
)


@Serializable
data class ReportHeader(
    val reportId: Int,
    val subjectName: String,
    val subjectId: Int,
    val groupName: String,
    val groupId: Int,
    val teacherName: String,
    val teacherLogin: String,
    val date: String,
    val module: String,
    val time: String,
    val status: String,
//    val ids: Int,
//    val isMentorWas: Boolean
)