package admin

import kotlinx.serialization.Serializable

@Serializable
data class CreateFormGroupsReceive(
    val formId: Int,
    val subjectId: Int,
    val groupId: Int,
)


@Serializable
data class CreateFormGroupsResponse(
    val groups: List<FormGroup>
)