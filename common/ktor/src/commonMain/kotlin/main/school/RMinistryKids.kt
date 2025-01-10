package main.school

import FIO
import admin.groups.forms.Form
import admin.groups.forms.FormGroup
import kotlinx.serialization.Serializable
import report.StudentNka


@Serializable
data class RUploadMinistryStup(
    val studentLogin: String,
    val stup: MinistryStup,
    val date: String,
    val edYear: Int
)


@Serializable
data class MinistryStup(
    val reason: String,
    val content: String,
    val reportId: Int?,
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
    val kids: List<MinistryKid>,
    val forms: List<Form>?
)

@Serializable
data class RMinistryListReceive(
    val date: String,
    val ministryId: String,
    val login: String?,
    val formId: Int?
)

data class MinistryListItem(
    val date: String,
    val ministryId: String,
    val kids: List<MinistryKid>
)