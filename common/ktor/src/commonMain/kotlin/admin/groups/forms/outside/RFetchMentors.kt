package admin.groups.forms.outside

import Person
import kotlinx.serialization.Serializable

@Serializable
data class RFetchMentorsResponse(
    val mentors: List<Person>
)
