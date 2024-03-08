package admin

import kotlinx.serialization.Serializable

@Serializable
data class FetchAllMentorsForGroupsResponse(
    val mentors: List<AdultForGroup>
)
