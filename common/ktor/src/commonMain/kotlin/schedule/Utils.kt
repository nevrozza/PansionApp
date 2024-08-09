package schedule

import FIO
import kotlinx.serialization.Serializable
import report.UserMark

@Serializable
data class ScheduleItem(
    val teacherLogin: String,
    val groupId: Int,
    val t: ScheduleTiming,
    val cabinet: Int,
    val teacherLoginBefore: String
)

@Serializable
data class PersonScheduleItem(
    val groupId: Int,
    val teacherFio: FIO,
    val cabinet: Int,
    val start: String,
    val end: String,
    val subjectName: String,
    val groupName: String,
    val marks: List<UserMark>,
    val stupsSum: Int,
    val isSwapped: Boolean
)

@Serializable
data class ScheduleTiming(
    val start: String,
    val end: String,
    val cabinetErrorGroupId: Int = 0,
    val studentErrors: List<StudentError> = emptyList()
)

@Serializable
data class StudentError(
    val groupId: Int,
    val logins: List<String>
)