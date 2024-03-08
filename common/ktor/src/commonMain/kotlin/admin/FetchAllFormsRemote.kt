package admin

import kotlinx.serialization.Serializable

@Serializable
data class FetchAllFormsResponse(
    val forms: List<Form>
)

@Serializable
data class Form(
    val id: Int,
    val name: String,
    val shortName: String,
    val mentorLogin: String,
    val classNum: Int
)
