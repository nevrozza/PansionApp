package admin

import kotlinx.serialization.Serializable

@Serializable
data class CreateNewGroupReceive(
    val name: String,
    val mentorLogin: String,
    val gSubjectId: Int,
    val difficult: String
)


@Serializable
data class CreateNewGroupResponse(
    val groups: List<SubjectGroup>
)