package report

import kotlinx.serialization.Serializable


@Serializable
data class RFetchDnevnikRuMarksReceive(
    val login: String,
    val quartersNum: String,
    val isQuarters: Boolean
)

@Serializable
data class RFetchDnevnikRuMarksResponse(
    val subjects: List<DnevnikRuMarksSubject>
)


@Serializable
data class DnevnikRuMarksSubject(
    val subjectId: Int,
    val subjectName: String,
    val marks: List<UserMark>, //UserMark
    val stupCount: Int,
    val stups: List<UserMark>
)

@Serializable
data class UserMark(
    val id: Int,
    val content: String,
    val reason: String,
    val isGoToAvg: Boolean,
    val groupId: Int,
    val date: String,
    val reportId: Int
)

@Serializable
data class UserMarkPlus(
    val mark: UserMark,
    val module: String,
    val deployDate: String,
    val deployTime: String,
    val deployLogin: String
)