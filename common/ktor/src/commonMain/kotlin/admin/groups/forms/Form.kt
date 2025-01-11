package admin.groups.forms

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

@Serializable
data class FormInit(
    val title: String,
    val shortTitle: String,
    val mentorLogin: String,
    val classNum: Int,
)

@Serializable
data class Form(
    val id: Int,
    val form: FormInit,
    val isActive: Boolean
)

@Serializable
data class CutedForm(
    val id: Int,
    val title: String,
    val classNum: Int
)

@JvmName("CutedFormListSort")
fun List<CutedForm>.formSort() = this.sortedWith(
    compareBy({ it.classNum }, { it.title })
)
@JvmName("FormListSort")
fun List<Form>.formSort() = this.sortedWith(
    compareBy({ it.form.classNum }, { it.form.title })
)