package admin.users

import admin.groups.forms.CutedForm
import kotlinx.serialization.Serializable



@Serializable
data class RFetchAllUsersResponse(
    val users: List<User>,
    val forms: List<CutedForm>
)



