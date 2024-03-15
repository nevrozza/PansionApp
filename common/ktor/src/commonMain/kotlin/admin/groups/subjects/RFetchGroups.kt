package admin.groups.subjects

import admin.groups.Group
import kotlinx.serialization.Serializable



@Serializable
data class RFetchGroupsReceive(
    val subjectId: Int
)


@Serializable
data class RFetchGroupsResponse(
    val groups: List<Group>
)

