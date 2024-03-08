package admin

import kotlinx.serialization.Serializable

@Serializable
data class FetchStudentsInFormReceive(
    val formId: Int
)


@Serializable
data class FetchStudentsInFormResponse(
    val students: List<Student>
)

