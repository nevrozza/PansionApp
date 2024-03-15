package admin.groups.students

import Person
import kotlinx.serialization.Serializable

@Serializable
data class RFetchStudentsInFormReceive(
    val formId: Int
)


@Serializable
data class RFetchStudentsInFormResponse(
    val students: List<Person>
)
