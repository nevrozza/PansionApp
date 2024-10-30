package main.school

import FIO
import kotlinx.serialization.Serializable

@Serializable
data class MinistryStudent(
    val ministryId: String,
    val fio: FIO,
    val login: String,
    val form: String
)

@Serializable
data class RFetchMinistrySettingsResponse(
    val students: List<MinistryStudent>
)

@Serializable
data class RCreateMinistryStudentReceive(
    val studentFIO: String,
    val login: String?,
    val ministryId: String
)
