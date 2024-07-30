package mentoring.preAttendance

import kotlinx.serialization.Serializable

@Serializable
data class RFetchPreAttendanceDayReceive(
    val studentLogin: String,
    val date: String
)

@Serializable
data class RFetchPreAttendanceDayResponse(
    val schedule: List<ScheduleForAttendance>,
    val attendance: ClientPreAttendance?
)

@Serializable
data class ClientPreAttendance(
    val start: String,
    val end: String,
    val reason: String,
    val isGood: Boolean
)

@Serializable
data class ScheduleForAttendance(
    val groupId: Int,
    val subjectName: String,
    val groupName: String,
    val start: String,
    val end: String
)