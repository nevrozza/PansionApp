package admin.calendar

import kotlinx.serialization.Serializable

@Serializable
data class CalendarModuleItem(
    val num: Int,
    val start: String,
    val halfNum: Int
)