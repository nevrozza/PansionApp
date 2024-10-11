import kotlinx.serialization.Serializable

@Serializable
data class ForAvg(
    val count: Int,
    val sum: Int
)