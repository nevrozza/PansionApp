package admin

import kotlinx.serialization.Serializable

@Serializable
data class CreateNewFormReceive(
    val name: String,
    val mentorLogin: String,
    val classNum: Int,
    val shortName: String
)


@Serializable
data class CreateNewFormResponse(
    val forms: List<Form>
)

