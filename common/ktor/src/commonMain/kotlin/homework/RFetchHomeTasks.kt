package homework

import admin.groups.Group
import admin.groups.Subject
import admin.groups.forms.CutedGroup
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class RFetchHomeTasksReceive(
    val login: String,
    val date: String?
)

@Serializable
data class RFetchHomeTasksResponse(
    val groups: List<CutedGroup>,
    val subjects: Map<Int, String>,
    val tasks: List<ClientHomeworkItem>,
    val dates: List<String>?
)

@Serializable
data class CutedDateTimeGroup(
    val id: Int,
    val name: String,
    val localDateTime: LocalDateTime
)
