package admin

import kotlinx.serialization.Serializable

@Serializable
data class FetchFormGroupsOfSubjectReceive(
    val subjectId: Int
)


@Serializable
data class FetchFormGroupsOfSubjectResponse(
    val groups: List<FormGroupOfSubject>
)

@Serializable
data class FormGroupOfSubject(
    val id: Int,
    val name: String,
)
