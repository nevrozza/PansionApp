package main

import kotlinx.serialization.Serializable

@Serializable
data class RChangeToUv(
    val login: String,
    val reportId: Int
)