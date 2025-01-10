package report

import kotlinx.serialization.Serializable

@Serializable
data class RFetchSubjectQuarterMarksReceive(
    val subjectId: Int,
    val login: String,
    val quartersNum: String,
    val edYear: Int
)

@Serializable
data class RFetchSubjectQuarterMarksResponse(
    val marks: List<UserMark>
)