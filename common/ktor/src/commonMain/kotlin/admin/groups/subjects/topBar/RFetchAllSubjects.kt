package admin.groups.subjects.topBar

import admin.groups.Subject
import kotlinx.serialization.Serializable

@Serializable
data class RFetchAllSubjectsResponse(
    val subjects: List<Subject>
)

