package main.school

import FIO
import kotlinx.serialization.Serializable

@Serializable
enum class MinistrySettingsReason {
    Form, School, Overview
}

@Serializable
data class MinistryStudent(
    val ministryId: String,
    val fio: FIO,
    val login: String,
    val form: String,
    val lvl: String
)

@Serializable
data class RFetchMinistrySettingsResponse(
    val students: List<MinistryStudent>
)
@Serializable
data class RFetchMinistryStudentsReceive(
    val reason: MinistrySettingsReason
)

@Serializable
data class RCreateMinistryStudentReceive(
    val studentFIO: String,
    val login: String?,
    val ministryId: String,
    val lvl: String,
    val reason: MinistrySettingsReason
)
