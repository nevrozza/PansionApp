package auth

import kotlinx.serialization.Serializable






@Serializable
data class StatsSettingsDTO(
    val login: String,
    val isOpened: Boolean
)

@Serializable
data class RChangeStatsSettingsReceive(
    val dto: StatsSettingsDTO
)