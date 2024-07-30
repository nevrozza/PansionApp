package homework

import kotlinx.serialization.Serializable

@Serializable
data class RCheckHomeTaskReceive(
    val login: String,
    val homeWorkId: Int,
    val id: Int?,
    val isCheck: Boolean
)