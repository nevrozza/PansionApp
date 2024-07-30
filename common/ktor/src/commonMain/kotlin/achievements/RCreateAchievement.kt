package achievements

import kotlinx.serialization.Serializable

@Serializable
data class RCreateAchievementReceive(
    val achievement: AchievementsDTO
)