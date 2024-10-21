package admin.groups.subjects

import FIO
import kotlinx.serialization.Serializable

@Serializable
data class RAddStudentToGroup(
    val fio: FIO,
    val groupId: Int,
    val subjectId: Int
)