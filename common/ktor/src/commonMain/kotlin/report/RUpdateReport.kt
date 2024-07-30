package report

import kotlinx.serialization.Serializable

@Serializable
data class RUpdateReportReceive(
    val lessonReportId: Int,
//    val groupId: Int,
//    val date: String,
//    val time: String,
    val topic: String,
    val description: String,
    val columnNames: List<String>,
    val status: Boolean,
    val ids: Int,
    val editTime: String,
    val isMentorWas: Boolean,
    val students: List<ServerStudentLine>,
    val marks: List<ServerRatingUnit>,
    val stups: List<ServerRatingUnit>
)


@Serializable
data class ServerRatingUnit(
    val login: String,
    val id: Int,
    val content: String,
    val reason: String,
    //val part: Int,
    val isGoToAvg: Boolean,
    val deployDate: String,
    val deployTime: String,
    val deployLogin: String
)

@Serializable
data class ServerStudentLine(
    val login: String,
    val lateTime: String,
    val isLiked: String,
    val attended: Attended?
)

@Serializable
data class Attended(
    val attendedType: String,
    val reason: String?
)

//@Serializable
//data class RUpdateReportResponse(
//    val token: String,
//    val login: String,
//    val user: UserInit
//)