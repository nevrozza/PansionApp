package admin

import kotlinx.serialization.Serializable

@Serializable
data class CreateNewGSubjectReceive(
    val name: String
)


@Serializable
data class CreateNewGSubjectResponse(
    val gSubjects: List<GSubject>
)
