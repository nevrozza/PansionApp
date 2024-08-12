package admin.groups.subjects

import kotlinx.serialization.Serializable

@Serializable
data class REditGroupReceive(
    val id: Int,
    val name: String,
    val mentorLogin: String,
    val difficult: String,
    val isActive: Boolean
)