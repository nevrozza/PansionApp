package admin.groups.forms

import admin.groups.Group
import kotlinx.serialization.Serializable

@Serializable
data class FormGroup(
    val groupId: Int,
    val groupName: String,
    val subjectId: Int
)
@Serializable
data class CutedGroup(
    val groupId: Int,
    val groupName: String,
    val isActive: Boolean
)