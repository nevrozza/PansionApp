package admin.cabinets

import FIO
import kotlinx.serialization.Serializable

@Serializable
data class RFetchCabinetsResponse(
    val cabinets: List<CabinetItem>
)

@Serializable
data class CabinetItem(
    val login: String,
    var cabinet: Int
)