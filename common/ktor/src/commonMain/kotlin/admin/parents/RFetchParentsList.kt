package admin.parents

import Person
import PersonParent
import kotlinx.serialization.Serializable

@Serializable
data class RFetchParentsListResponse(
    val users: List<PersonParent>,
    val lines: List<ParentLine>
)

@Serializable
data class ParentLine(
    val id: Int,
    val studentLogin: String,
    val parentLogin: String
)