import kotlinx.serialization.Serializable

@Serializable
data class RFetchGroupDataReceive(
    val groupId: Int
)

@Serializable
data class RFetchGroupDataResponse(
    val groupName: String,
    val subjectId: Int?,
    val subjectName: String,
    val teacherLogin: String,
)