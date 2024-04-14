package admin.groups.students.deep

import kotlinx.serialization.Serializable

@Serializable
data class RCreateStudentGroupReceive(
    val studentLogin: String,
    val subjectId: Int,
    val groupId: Int,
)


//@Serializable
//data class RCreateFormGroupsResponse(
//    val groups: List<FormGroup>
//)