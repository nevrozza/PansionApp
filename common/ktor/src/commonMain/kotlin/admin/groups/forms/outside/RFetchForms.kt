package admin.groups.forms.outside

import admin.groups.forms.Form
import kotlinx.serialization.Serializable

@Serializable
data class RFetchFormsResponse(
    val forms: List<Form>
)


