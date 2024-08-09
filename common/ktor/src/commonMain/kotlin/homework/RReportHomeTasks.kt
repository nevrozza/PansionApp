package homework

import kotlinx.serialization.Serializable

@Serializable
data class RSaveReportHomeTasksReceive(
    val subjectId: Int,
    val groupId: Int,
    val reportId: Int,
    val tasks: List<CreateReportHomeworkItem>
)

@Serializable
data class RFetchReportHomeTasksReceive(
    val reportId: Int
)

@Serializable
data class RFetchReportHomeTasksResponse(
    val tasks: List<CreateReportHomeworkItem>
)


@Serializable
data class RFetchGroupHomeTasksReceive(
    val groupId: Int
)

@Serializable
data class RFetchGroupHomeTasksResponse(
    val tasks: List<ClientReportHomeworkItem>
)