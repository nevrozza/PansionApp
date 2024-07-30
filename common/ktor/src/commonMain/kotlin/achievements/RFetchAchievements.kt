package achievements

import Person
import kotlinx.serialization.Serializable

@Serializable
data class RFetchAchievementsResponse(
    val list: List<AchievementsDTO>,
    val students: List<Person>?,
    val subjects: Map<Int, String>
)

@Serializable
data class RFetchAchievementsForStudentReceive(
    val studentLogin: String
)
