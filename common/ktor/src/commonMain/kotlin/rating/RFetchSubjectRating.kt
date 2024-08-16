package rating

import kotlinx.serialization.Serializable


// 0 - week
// 1 - module
// 2 - year
@Serializable
data class RFetchSubjectRatingReceive(
    val login: String,
    val subjectId: Int,
    val period: Int,
    val forms: Int
)

@Serializable
data class RFetchSubjectRatingResponse(
    val hash: HashMap<Int, List<RatingItem>>,
    val me: HashMap<Int, Pair<Int, Int>?>,
    val lastTimeEdit: String
)
