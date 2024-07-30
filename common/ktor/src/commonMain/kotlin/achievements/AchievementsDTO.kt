package achievements

import kotlinx.serialization.Serializable

@Serializable
data class AchievementsDTO(
    val id: Int,
    val studentLogin: String,
    val creatorLogin: String,
    val date: String,
    val time: String,

    val text: String,
    val showInProfile: Boolean,
    val showDate: String?,

    val subjectId: Int,
    val stups: Int
)
