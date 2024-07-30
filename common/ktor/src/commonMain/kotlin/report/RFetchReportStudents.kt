package report

import kotlinx.serialization.Serializable

@Serializable
data class RFetchReportStudentsReceive(
    val reportId: Int,
    val module: Int,
    val date: String,
    val minutes: Int
)

@Serializable
data class RFetchReportStudentsResponse(
    val students: List<AddStudentLine>,
    val marks: List<ServerRatingUnit>,
    val stups: List<ServerRatingUnit>
)

@Serializable
data class AddStudentLine(
    val serverStudentLine: ServerStudentLine,
    val shortFio: String,
    val prevSum: Int,
    val prevCount: Int,
)