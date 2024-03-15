package admin.groups.subjects

import Person
import kotlinx.serialization.Serializable

@Serializable
data class RFetchTeachersResponse(
    val teachers: List<Person>
)

