package webload

import FIO
import kotlinx.serialization.Serializable

@Serializable
data class RFetchUserDataReceive(
    val login: String
)

@Serializable
data class RFetchUserDataResponse(
    val fio: FIO?,
    val avatarId: Int
)