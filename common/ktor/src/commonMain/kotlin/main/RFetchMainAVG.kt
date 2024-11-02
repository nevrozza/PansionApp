package main

import kotlinx.serialization.Serializable

@Serializable
data class RFetchMainAVGReceive(
    val login: String,
    val period: String,
    val isFirst: Boolean
)

@Serializable
data class RFetchMainAVGResponse(
    val avg: Float,
    val stups: Int,
    val achievementsStups: Map<Period, Pair<Int, Int>>?
)

@Serializable
enum class Period {
    WEEK, MODULE, HALF_YEAR, YEAR
}
