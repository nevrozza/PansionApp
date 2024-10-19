package report

import kotlinx.serialization.Serializable

@Serializable
data class RFetchAllGroupMarksReceive(
    val subjectId: Int,
    val groupId: Int
)

@Serializable
data class RFetchAllGroupMarksResponse(
    val students: List<AllGroupMarksStudent>,
    val firstHalfNums: List<Int>
)

@Serializable
data class AllGroupMarksStudent(
    val login: String,
    val shortFIO: String,
    val isQuarters: Boolean,
    val marks: List<UserMarkPlus>,
    val stups: List<UserMarkPlus>,
    val nki: List<StudentNka>
)

@Serializable
data class StudentNka(
    val date: String,
    val module: String,
    val isUv: Boolean,
    val groupId: Int? = null
)