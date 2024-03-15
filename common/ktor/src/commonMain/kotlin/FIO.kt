import kotlinx.serialization.Serializable

@Serializable
data class FIO(
    val name: String,
    val surname: String,
    val praname: String?
)
