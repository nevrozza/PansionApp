package admin.schedule

import FIO
import admin.groups.Group
import kotlinx.serialization.Serializable

@Serializable
data class RFetchInitScheduleResponse(
    val teachers: List<SchedulePerson>,
    val students: List<SchedulePerson>,
    val groups: List<ScheduleGroup>,
    val subjects: List<ScheduleSubject>
)

@Serializable
data class SchedulePerson(
    val login: String,
    val fio: FIO,
    val groups: List<Int>
)

@Serializable
data class ScheduleGroup(
    val id: Int,
    val subjectId: Int,
    val name: String
)

@Serializable
data class ScheduleSubject(
    val id: Int,
    val name: String
)