package journal.init

import kotlinx.serialization.Serializable

@Serializable
data class FetchStudentsInGroupReceive(
    val groupId: Int
)

@Serializable
data class FetchStudentsInGroupResponse(
    val students: List<StudentInGroup>
)

@Serializable
data class StudentInGroup(
    val login: String,
    val name: String,
    val surname: String,
    val praname: String?,
    val isActivated: Boolean
)
