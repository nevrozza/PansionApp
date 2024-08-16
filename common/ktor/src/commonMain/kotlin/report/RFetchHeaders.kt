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
    val status: Boolean,
    val theme: String
)

@Serializable
data class RFetchStudentReportReceive(
    val login: String,
    val reportId: Int
)

@Serializable
data class RFetchStudentReportResponse(
    val marks: List<UserMarkPlus>,
    val stups: List<UserMarkPlus>,
    val studentLine: ClientStudentLine,
    val info: StudentReportInfo,
    val homeTasks: List<String>
)

@Serializable
data class StudentReportInfo(
    val reportId: Int,
    val subjectName: String,
    val groupName: String,
    val teacherName: String,
    val date: String,
    val module: String,
    val time: String,
    val theme: String
)