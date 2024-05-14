package rating

import FIO
import kotlinx.serialization.Serializable

@Serializable
data class RatingItem(
    val login: String,
    val fio: FIO,
    val avatarId: Int,
    val stups: Int,
    val top: Int,
    val groupName: String,
    val formNum: Int,
    val formShortTitle: String,
    val avg: String
)