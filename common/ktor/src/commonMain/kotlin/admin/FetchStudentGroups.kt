package admin

import kotlinx.serialization.Serializable

@Serializable
data class FetchStudentGroupsOfStudentReceive(
    val studentLogin: String
)


@Serializable
data class FetchStudentGroupsOfStudentResponse(
    val groups: List<SubjectGroup>
)