package admin.groups.students

import kotlinx.serialization.Serializable

@Serializable
data class RBindStudentToFormReceive(
    val studentLogin: String,
    val formId: Int
)


//@Serializable
//data class CreateUserFormResponse(
//    val students: List<Person>
//)