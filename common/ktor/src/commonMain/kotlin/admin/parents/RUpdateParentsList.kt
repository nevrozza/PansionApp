package admin.parents

import kotlinx.serialization.Serializable

@Serializable
data class RUpdateParentsListReceive(
    val studentLogin: String,
    val id: Int,
    val parentLogin: String
)