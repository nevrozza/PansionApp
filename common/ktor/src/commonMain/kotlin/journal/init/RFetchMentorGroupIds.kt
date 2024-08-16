package journal.init

import kotlinx.serialization.Serializable

@Serializable
data class RFetchMentorGroupIdsResponse(
    val ids: List<Int>
)