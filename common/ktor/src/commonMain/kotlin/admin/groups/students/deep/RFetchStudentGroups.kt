package admin.groups.students.deep

import admin.groups.Group
import kotlinx.serialization.Serializable

@Serializable
data class RFetchStudentGroupsReceive(
    val studentLogin: String
)


@Serializable
data class RFetchStudentGroupsResponse(
    val groups: List<Group>
)