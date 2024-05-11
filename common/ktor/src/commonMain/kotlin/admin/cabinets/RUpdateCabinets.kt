package admin.cabinets

import kotlinx.serialization.Serializable

@Serializable
data class RUpdateCabinetsReceive(
    val cabinets: List<CabinetItem>
)