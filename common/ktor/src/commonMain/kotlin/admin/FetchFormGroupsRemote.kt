package admin

import kotlinx.serialization.Serializable

@Serializable
data class FetchFormGroupsReceive(
    val formId: Int
)


@Serializable
data class FetchFormGroupsResponse(
    val groups: List<FormGroup>
)

@Serializable
data class FormGroup(
    val id: Int,
    val name: String,
    val gSubjectId: Int,
)
