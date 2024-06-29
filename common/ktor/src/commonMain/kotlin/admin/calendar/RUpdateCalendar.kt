package admin.calendar

import kotlinx.serialization.Serializable

@Serializable
data class RUpdateCalendarReceive(
    val items: List<CalendarModuleItem>
)