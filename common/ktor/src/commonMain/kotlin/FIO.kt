import kotlinx.serialization.Serializable

@Serializable
data class FIO(
    @Serializable
    val name: String,
    @Serializable
    val surname: String,
    @Serializable
    val praname: String?
)
