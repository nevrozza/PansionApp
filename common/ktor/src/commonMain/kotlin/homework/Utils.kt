package homework

import kotlinx.serialization.Serializable


@Serializable
data class ClientHomeworkItem(
    val id: Int,
    val date: String,
    val time: String,
    val subjectId: Int,
    val type: String,
    val groupId: Int,
    val text: String,
    val stups: Int,
    val fileIds: List<Int>?,
    val doneId: Int?,
    val seconds: Int,
    val done: Boolean,
    val isNec: Boolean
)

@Serializable
data class ClientReportHomeworkItem(
    val id: Int,
    val date: String,
    val time: String,
    val subjectId: Int,
    val type: String,
    val groupId: Int,
    val text: String,
    val stups: Int,
    val fileIds: List<Int>?,
    val studentLogins: List<String>?,
    val isNec: Boolean
)

@Serializable
data class CreateReportHomeworkItem(
    val id: Int,
    val isNew: Boolean,
    val type: String,
    val text: String,
    val stups: Int,
    val fileIds: List<Int>?,
    val studentLogins: List<String>?,
    val isNec: Boolean
)