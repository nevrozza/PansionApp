package admin.groups.forms

import kotlinx.serialization.Serializable

@Serializable
data class RFetchCutedGroupsResponse(
    val groups: List<CutedGroup>
)