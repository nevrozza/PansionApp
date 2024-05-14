package rating

import admin.schedule.ScheduleSubject
import kotlinx.serialization.Serializable

@Serializable
data class RFetchScheduleSubjectsResponse(
    val subjects: List<ScheduleSubject>
)