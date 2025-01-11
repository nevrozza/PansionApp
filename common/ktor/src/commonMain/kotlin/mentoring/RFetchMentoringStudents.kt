package mentoring

import MentorPerson
import kotlinx.serialization.Serializable
import registration.RegistrationRequest
import kotlin.jvm.JvmName

@Serializable
data class RFetchMentoringStudentsResponse(
    val forms: List<MentorForms>,
    val students: List<MentorPerson>,
    val requests: List<RegistrationRequest>,
)



@Serializable
data class MentorForms(
    val id: Int,
    val num: Int,
    val title: String,
    val isQrActive: Boolean
)

@JvmName("MentorFormListSort")
fun List<MentorForms>.formSort() = this.sortedWith(
    compareBy({ it.num }, { it.title })
)