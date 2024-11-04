package journal.init

import Person
import kotlinx.serialization.Serializable

@Serializable
data class RFetchStudentsInGroupReceive(
    val groupId: Int,
    val date: String?,
    val lessonId: Int?
)

@Serializable
data class PersonForGroup(
    val p: Person,
    val isDeleted: Boolean
)

@Serializable
data class RFetchStudentsInGroupResponse(
    val students: List<PersonForGroup>
)
