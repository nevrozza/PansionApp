package rating

import kotlinx.serialization.Serializable


// 0 - week
// 1 - module
// 2 - year
@Serializable
data class RFetchSubjectRatingReceive(
    val login: String,
    val subjectId: Int,
    val period: PansionPeriod?,
    val forms: Int
)

@Serializable
data class RFetchSubjectRatingResponse(
    val hash: Map<String, Map<Int, List<RatingItem>>>,
    val me: Map<String, Map<Int, RatingItem?>>,
    val lastTimeEdit: String
)
