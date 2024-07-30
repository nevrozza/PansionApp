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
    val tasks: List<ClientHomeworkItem>
)

@Serializable
data class CutedDateTimeGroup(
    val id: Int,
    val name: String,
    val localDateTime: LocalDateTime?
)
