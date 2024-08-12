package admin.schedule

import FIO
import admin.groups.Group
import kotlinx.serialization.Serializable

@Serializable
data class RFetchInitScheduleResponse(
    val teachers: List<SchedulePerson>,
    val students: List<SchedulePerson>,
    val groups: List<ScheduleGroup>,
    val subjects: List<ScheduleSubject>,
    val forms: HashMap<Int, ScheduleFormValue>
)

@Serializable
data class SchedulePerson(
    val login: String,
    val fio: FIO,
    val groups: List<Pair<Int, Boolean>>
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
    val name: String,
    val isActive: Boolean
)


@Serializable
data class ScheduleFormValue(
    val num: Int,
    val shortTitle: String,
    val logins: List<String>
)