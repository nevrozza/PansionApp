package admin.groups.subjects

import admin.groups.GroupInit
import kotlinx.serialization.Serializable

@Serializable
data class RCreateGroupReceive(
    val group: GroupInit
)
