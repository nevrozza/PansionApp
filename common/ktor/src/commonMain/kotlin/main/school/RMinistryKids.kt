package main.school

import FIO
import kotlinx.serialization.Serializable
import report.StudentNka


@Serializable
data class RUploadMinistryStup(
    val studentLogin: String,
    val stup: MinistryStup,
    val date: String
)


@Serializable
data class MinistryStup(
    val reason: String,
    val content: String,
    val reportId: Int,
    val custom: String?
)

@Serializable
data class MinistryLesson(
    val reportId: Int,
    val subjectName: String,
    val groupName: String,
    val time: String,
    val isUvNka: Boolean?,
    val lateTime: String,
    val isLiked: String
)

@Serializable
data class MinistryKid(
    val fio: FIO,
    val formId: Int,
    val formTitle: String,
    val login: String,
    val lessons: List<MinistryLesson>,
    val dayStups: List<MinistryStup>,
//    val dayStupsCount: Int,
    val weekStupsCount: Int,
    val moduleStupsCount: Int,
    val yearStupsCount: Int
)

@Serializable
data class RMinistryListResponse(
//    val date: String,
//    val ministryId: String,
    val kids: List<MinistryKid>
)

@Serializable
data class RMinistryListReceive(
    val date: String,
    val ministryId: String,
)