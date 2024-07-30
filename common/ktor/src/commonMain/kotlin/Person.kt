import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val login: String,
    val fio: FIO,
    val isActive: Boolean
)

@Serializable
data class MentorPerson(
    val login: String,
    val fio: FIO,
    val avatarId: Int,
    val formId: Int
)