package main

import kotlinx.serialization.Serializable

@Serializable
data class RFetchSchoolDataReceive(
    val login: String
)

@Serializable
data class RFetchSchoolDataResponse(
    val formId: Int?,
    val formName: String?,
    val top: Int?,
    val formNum: Int?
)