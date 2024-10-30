package main.school

import kotlinx.serialization.Serializable

@Serializable
data class RFetchMinistryHeaderInitResponse(
    val isMultiMinistry: Boolean,
    val pickedMinistry: String
)