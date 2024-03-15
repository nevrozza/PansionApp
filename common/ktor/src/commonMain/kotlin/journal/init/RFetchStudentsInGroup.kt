package journal.init

import Person
import kotlinx.serialization.Serializable

@Serializable
data class RFetchStudentsInGroupReceive(
    val groupId: Int
)

@Serializable
data class RFetchStudentsInGroupResponse(
    val students: List<Person>
)
