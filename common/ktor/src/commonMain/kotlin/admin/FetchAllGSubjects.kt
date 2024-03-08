package admin

import kotlinx.serialization.Serializable

@Serializable
data class FetchAllGSubjectsResponse(
    val gSubjects: List<GSubject>
)

@Serializable
data class GSubject(
    val id: Int,
    val name: String,
    val isActivated: Boolean
)