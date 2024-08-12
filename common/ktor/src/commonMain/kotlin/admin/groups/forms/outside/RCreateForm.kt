package admin.groups.forms.outside

import admin.groups.forms.FormInit
import kotlinx.serialization.Serializable

@Serializable
data class CreateFormReceive(
    val form: FormInit
)

@Serializable
data class REditFormReceive(
    val id: Int,
    val form: FormInit
)


//@Serializable
//data class CreateNewFormResponse(
//    val forms: List<Form>
//)

