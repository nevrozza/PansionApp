package schedule

import kotlinx.serialization.Serializable

@Serializable
data class RFetchScheduleDateReceive(
    val dayOfWeek: String,
    val day: String,
    val isFirstTime: Boolean
)
@Serializable
data class RFetchPersonScheduleReceive(
    val login: String,
    val dayOfWeek: String,
    val day: String
)