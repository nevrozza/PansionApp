package rating

import FIO
import kotlinx.serialization.Serializable
import server.twoNums

@Serializable
data class RatingItem(
    val login: String,
    val fio: FIO,
    val avatarId: Int,
    val stups: Int,
    val top: Int,
    val groupName: String,
    val formNum: Int,
    val formShortTitle: String,
    val avg: String,
    val avgAlg: Float,
    val stupsAlg: Float,
    val topAvg: Int,
    val topStups: Int,

    val difficulty: Int,
)


@Serializable
sealed interface PansionPeriod {
    @Serializable
    data class Week(val num: Int) : PansionPeriod

    @Serializable
    data class Module(val num: Int) : PansionPeriod

    @Serializable
    data class Half(val num: Int) : PansionPeriod

    @Serializable
    data object Year : PansionPeriod
}

fun PansionPeriod.toStr(): String {
    return when (this) {
        is PansionPeriod.Half -> "h${this.num}"
        is PansionPeriod.Module -> "m${this.num.twoNums()}"
        is PansionPeriod.Week -> "w${this.num.twoNums()}"
        PansionPeriod.Year -> "y"
    }
}

fun String.toPeriod(): PansionPeriod {
    val num = this.removePrefix("h")
        .removePrefix("m")
        .removePrefix("w")
        .removePrefix("y").toIntOrNull() ?: 1
    return when (this[0]) {
        'h' -> PansionPeriod.Half(num)
        'm' -> PansionPeriod.Module(num)
        'w' -> PansionPeriod.Week(num)

        'y' -> PansionPeriod.Year
        else -> rating.PansionPeriod.Year
    }
}