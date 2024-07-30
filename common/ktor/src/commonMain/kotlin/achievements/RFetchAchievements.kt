package achievements

import kotlinx.serialization.Serializable

@Serializable
data class RFetchAchievementsResponse(
    val list: List<AchievementsDTO>
)

@Serializable
data class RFetchAchievementsForStudentReceive(
    val studentLogin: String
)
