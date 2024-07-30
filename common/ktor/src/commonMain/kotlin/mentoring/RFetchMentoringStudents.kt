package mentoring

import MentorPerson
import kotlinx.serialization.Serializable

@Serializable
data class RFetchMentoringStudentsResponse(
    val forms: List<MentorForms>,
    val students: List<MentorPerson>
)



@Serializable
data class MentorForms(
    val id: Int,
    val num: Int,
    val title: String
)