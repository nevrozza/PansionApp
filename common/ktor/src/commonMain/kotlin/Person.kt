import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val login: String,
    val fio: FIO,
    val isActive: Boolean
)