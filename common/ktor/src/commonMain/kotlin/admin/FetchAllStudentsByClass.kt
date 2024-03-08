package admin

import kotlinx.serialization.Serializable

@Serializable
data class FetchAllStudentsByClassReceive(
    val classNum: Int
)


@Serializable
data class FetchAllStudentsByClassResponse(
    val students: List<Student>
)

@Serializable
data class Student(
    val login: String,
    val name: String,
    val surname: String,
    val praname: String?,
//    val isActivated: Boolean
)