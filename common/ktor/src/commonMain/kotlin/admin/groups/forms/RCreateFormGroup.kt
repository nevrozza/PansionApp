package admin.groups.forms

import kotlinx.serialization.Serializable

@Serializable
data class RCreateFormGroupReceive(
    val formId: Int,
    val subjectId: Int,
    val groupId: Int,
)


//@Serializable
//data class RCreateFormGroupsResponse(
//    val groups: List<FormGroup>
//)