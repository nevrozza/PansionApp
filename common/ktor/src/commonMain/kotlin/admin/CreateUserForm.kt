package admin

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserFormReceive(
    val currentFormId: Int,
    val studentLogin: String,
    val hisFormId: Int
)


@Serializable
data class CreateUserFormResponse(
    val students: List<Student>
)