package admin.groups.forms

import kotlinx.serialization.Serializable

@Serializable
data class RFetchFormGroupsReceive(
    val formId: Int
)


@Serializable
data class RFetchFormGroupsResponse(
    val groups: List<FormGroup>
)
