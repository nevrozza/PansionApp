package rating

import admin.calendar.Holiday
import admin.schedule.ScheduleSubject
import kotlinx.serialization.Serializable

@Serializable
data class RFetchScheduleSubjectsResponse(
    val subjects: List<ScheduleSubject>,
    val holiday: List<Holiday>,
    val currentModule: Int,
    val currentHalf: Int
)