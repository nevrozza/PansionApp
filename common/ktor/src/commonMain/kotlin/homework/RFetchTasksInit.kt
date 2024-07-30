package homework

import kotlinx.serialization.Serializable

@Serializable
data class RFetchTasksInitReceive(
    val login: String
)

@Serializable
data class RFetchTasksInitResponse(
    val groups: List<CutedDateTimeGroup>,
    val subjects: Map<Int, String>,
    val dates: List<String>
)