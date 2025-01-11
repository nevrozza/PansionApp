package admin.parents

import Person
import PersonParent
import admin.groups.forms.CutedForm
import kotlinx.serialization.Serializable

@Serializable
data class RFetchParentsListResponse(
    val users: List<PersonParent>,
    val lines: List<ParentLine>,
    val forms: List<CutedForm>
)

@Serializable
data class ParentLine(
    val id: Int,
    val studentLogin: String,
    val parentLogin: String
)