package rating

import FIO
import kotlinx.serialization.Serializable
import ForAvg

@Serializable
data class RFetchFormRatingReceive(
    val formId: Int,
    val formNum: Int,
    val period: Int
)

@Serializable
data class RFetchFormRatingResponse(
    val students: List<FormRatingStudent>,
    val subjects: Map<Int, String>
    //val achivki
    //val lines
)

@Serializable
data class FormRatingStudent(
    val login: String,
    val fio: FIO,
    val avatarId: Int,
    val formTitle: String?,
    val avg: ForAvg,
    val edStups: List<FormRatingStup>,
//    val likes: Int,
//    val dislikes: Int
    //mvdStups
    //zdrStups
    )

@Serializable
data class FormRatingStup(
    val subjectId: Int,
    val reason: String,
    val date: String,
    val content: String
)