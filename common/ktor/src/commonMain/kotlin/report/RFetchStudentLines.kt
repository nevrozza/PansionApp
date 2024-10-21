package report

import kotlinx.serialization.Serializable

@Serializable
data class RFetchStudentLinesReceive(
    val login: String
)

@Serializable
data class RFetchStudentLinesResponse(
    val studentLines: List<ClientStudentLine>
)

@Serializable
data class ClientStudentLine(
    val reportId: Int,
    val lateTime: String,
    val isLiked: String,
    val attended: String?,
    val subjectName: String,
    val groupName: String,
    val time: String,
    val date: String,
    val login: String,
    val topic: String
)