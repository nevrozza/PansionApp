package rating

import admin.groups.forms.CutedForm
import kotlinx.serialization.Serializable

@Serializable
data class RFetchFormsForFormResponse(
    val forms: List<CutedForm>
)

//@Serializable
//data class CutedForm(
//    val id: Int,
//    val formName: String
//)