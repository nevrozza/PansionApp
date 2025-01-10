package admin.calendar

import kotlinx.serialization.Serializable

@Serializable
data class RFetchCalendarResponse(
    val items: List<CalendarModuleItem>,
    val holidays: List<Holiday>,
)