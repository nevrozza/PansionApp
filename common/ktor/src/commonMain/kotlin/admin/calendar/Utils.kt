package admin.calendar

import kotlinx.serialization.Serializable

@Serializable
data class CalendarModuleItem(
    val num: Int,
    val start: String,
    val halfNum: Int
)

@Serializable
data class Holiday(
    val id: Int,
    val edYear: Int,
    val start: String,
    val end: String,
    val isForAll: Boolean
)
