package report

import kotlinx.serialization.Serializable

@Serializable
data class RFetchDetailedStupsReceive(
    val login: String,
    val edYear: Int
)

@Serializable
data class RFetchDetailedStupsResponse(
    val stups: List<DetailedStupsSubject>
)

@Serializable
data class DetailedStupsSubject(
    val subjectName: String,
    val stups: List<UserMark>
)