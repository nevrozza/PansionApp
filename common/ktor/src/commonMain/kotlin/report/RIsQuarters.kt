package report

import kotlinx.serialization.Serializable

@Serializable
data class RIsQuartersReceive(
    val login: String
)

@Serializable
data class RIsQuartersResponse(
    val isQuarter: Boolean
)