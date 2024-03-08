package admin

import kotlinx.serialization.Serializable

@Serializable
data class FetchAllTeachersForGroupsResponse(
    val teachers: List<AdultForGroup>
)

@Serializable
data class AdultForGroup(
    val login: String,
    val name: String,
    val surname: String,
    val praname: String?
)