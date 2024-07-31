package main

import kotlinx.serialization.Serializable

@Serializable
data class RFetchMainHomeTasksCountReceive(
    val studentLogin: String
)

@Serializable
data class RFetchMainHomeTasksCountResponse(
    val count: Int
)