package schedule

import kotlinx.serialization.Serializable

@Serializable
data class RFetchScheduleDateReceive(
    val dayOfWeek: String,
    val day: String
)