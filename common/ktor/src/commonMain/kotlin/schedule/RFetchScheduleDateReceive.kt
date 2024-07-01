package schedule

import kotlinx.serialization.Serializable

@Serializable
data class RFetchScheduleDateReceive(
    val dayOfWeek: String,
    val day: String
)
@Serializable
data class RFetchPersonScheduleReceive(
    val login: String,
    val dayOfWeek: String,
    val day: String
)