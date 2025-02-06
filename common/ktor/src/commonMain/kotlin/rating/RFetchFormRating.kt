    package rating

import FIO
import kotlinx.serialization.Serializable

    @Serializable
data class RFetchFormRatingReceive(
    val formId: Int,
    val formNum: Int,
    val period: PansionPeriod?
)

@Serializable
data class RFetchFormRatingResponse(
    val students: List<FormRatingStudent>,
    val subjects: Map<Int, String>,
    val currentWeek: Int,
    val currentModule: Int,
    val currentHalf: Int
    //val achivki
    //val lines
)

@Serializable
data class FormRatingStudent(
    val login: String,
    val fio: FIO,
    val avatarId: Int,
    val formTitle: String?,
    val avg: String,
    val avgAlg: Float,
    val stupsAlg: Float,

    val topAvg: Int = 0,
    val topStups: Int = 0,
    val top: Int = 0,



    val edStups: List<FormRatingStup>,
    val mvdStupsCount: Int,
    val zdStupsCount: Int,
//    val likes: Int,
//    val dislikes: Int
    //mvdStups
    //zdrStups
    )

@Serializable
data class FormRatingStup(
    val subjectId: Int?,
    val reason: String,
    val date: String,
    val content: String
)