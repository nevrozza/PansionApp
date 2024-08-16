package auth

import kotlinx.serialization.Serializable

@Serializable
data class RCheckGIASubjectReceive(
    val login: String,
    val subjectId: Int,
    val isChecked: Boolean
)