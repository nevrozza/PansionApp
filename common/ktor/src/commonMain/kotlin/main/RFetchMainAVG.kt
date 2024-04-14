package main

import kotlinx.serialization.Serializable

@Serializable
data class RFetchMainAVGReceive(
    val login: String,
    val period: String
)

@Serializable
data class RFetchMainAVGResponse(
    val avg: Float,
    val stups: Pair<Int, Int>
)