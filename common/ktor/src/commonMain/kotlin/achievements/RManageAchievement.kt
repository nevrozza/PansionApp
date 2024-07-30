package achievements

import kotlinx.serialization.Serializable

@Serializable
data class REditAchievementReceive(
    val id: Int,
    val studentLogin: String,
    val subjectId: Int,
    val stups: Int
)

@Serializable
data class RUpdateGroupOfAchievementsReceive(
    val oldText: String,
    val oldShowDate: String,
    val oldDate: String,
    val newText: String,
    val newShowDate: String,
    val newDate: String,
)

@Serializable
data class RDeleteAchievementReceive(
    val id: Int
)
//val id: Int,
//    val studentLogin: String,
//    val creatorLogin: String,
//    val date: String,
//    val time: String,
//
//    val text: String,
//    val showInProfile: Boolean,
//    val showDate: String?,
//
//    val subjectId: Int,
//    val stups: Int