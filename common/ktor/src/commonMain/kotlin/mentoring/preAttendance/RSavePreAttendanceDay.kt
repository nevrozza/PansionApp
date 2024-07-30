package mentoring.preAttendance

import kotlinx.serialization.Serializable

@Serializable
data class RSavePreAttendanceDayReceive(
    val studentLogin: String,
    val date: String,
    val preAttendance: ClientPreAttendance
)