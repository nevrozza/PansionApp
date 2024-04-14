package report

import kotlinx.serialization.Serializable


@Serializable
data class RFetchDnevnikRuMarksReceive(
    val login: String,
    val quartersNum: String
)

@Serializable
data class RFetchDnevnikRuMarksResponse(
    val subjects: List<DnevnikRuMarksSubject>
)


@Serializable
data class DnevnikRuMarksSubject(
    val subjectId: Int,
    val subjectName: String,
    val marks: List<UserMark>,
    val stupCount: Int
)

@Serializable
data class UserMark(
    val id: Int,
    val content: String,
    val reason: String,
    val isGoToAvg: Boolean,
    val groupId: Int,
    val date: String
)