package server

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.pow
import kotlin.math.roundToInt

fun getWeekDays(): List<String> {
    val today = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3")).date
    val days = mutableListOf<LocalDate>()
    val firstWeekDay = today.daysShift(-DayOfWeek.values().indexOf(today.dayOfWeek))
    for (i in 0 until DayOfWeek.values().count()) {
        days.add(firstWeekDay.daysShift(i))
    }
    return days.map { time ->
        "${time.dayOfMonth.twoNums()}." +
                "${time.monthNumber.twoNums()}." +
                "${time.year}"
    }
}


fun fetchReason(reasonId: String): String {
    return when(reasonId.subSequence(0, 3)) {
        "!dz" -> "ДЗ"
        "!cl" -> "Кл/Р"
        "!st" -> "Ступени"
        "!ds" -> "Дисциплина"
        else -> "null"
    } + ": " + fetchTitle(reasonId)
}

fun fetchTitle(reasonId: String): String {
    return when (reasonId.subSequence(0, 3)) {
        "!dz" -> {
            when (reasonId.last()) {
                '1' -> "Тест"
                '2' -> "Письм. работа"
                '3' -> "Устный ответ"
                '4' -> "Решение задач"
                else -> "null"
            }
        }

        "!cl" -> {
            when (reasonId.last()) {
                '1' -> "Тест"
                '2' -> "Письм. работа"
                '3' -> "Работа на уроке"
                '4' -> "С/Р"
                '5' -> "К/Р"
                else -> "null"
            }
        }

        "!st" -> {
            when(reasonId.last()) {
                '1' -> "ДЗ"
                '2' -> "М/К"
                '3' -> "Тетрадь"
                '4' -> "Урок"
                '5' -> "Рост"
                else -> "null"
            }
        }

        "!ds" -> {
            when(reasonId.last()) {
                '1' -> "Готовность"
                '2' -> "Поведение"
                '3' -> "Нарушение"
                else -> "null"
            }
        }

        else -> "null"
    }

}

fun LocalDate.daysShift(days: Int): LocalDate = when {
    days < 0 -> {
        minus(DateTimeUnit.DayBased(-days))
    }
    days > 0 -> {
        plus(DateTimeUnit.DayBased(days))
    }
    else -> this
}

fun Int.twoNums(): String {
    return if (this < 10) {
        "0$this"
    } else this.toString()
}

//123456789012345
//00:00-18-sat-13
fun getSixteenTime(): String {
    val time = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3"))
    return "${time.hour.twoNums()}:" +
            "${time.minute.twoNums()}-" +
            "${time.dayOfMonth.twoNums()}-" +
            "${time.month.toString().subSequence(0, 3)}-" +
            "${time.year.toString().subSequence(2, 4)}"
}
fun getSixTime(): String {
    val time = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3"))
    return "${time.hour.twoNums()}:" +
            time.minute.twoNums()
}
fun getDate(): String {
    val time = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3"))
    return "${time.dayOfMonth.twoNums()}." +
            "${time.monthNumber.twoNums()}." +
            "${time.year}"
}

fun Float.roundTo(numFractionDigits: Int): Float {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return ((this * factor).roundToInt() / factor).toFloat()
}

fun Int.toSixTime(): String {
    val hour = this/60
    println("Horu: $hour")
    val minutes = this - 60*hour

    println("mins: $minutes")
    return "${hour.twoNums()}:" +
            minutes.twoNums()
}

fun String.cut(size: Int): String {
    return if (this.length > size) {
        this.subSequence(0, size).toString()
    } else {
        this
    }
}

fun String.latin() = this.replace("а", "a")
    .replace("б", "b")
    .replace("в", "v")
    .replace("г", "g")
    .replace("д", "d")
    .replace("е", "e")
    .replace("ё", "yo")
    .replace("ж", "j")
    .replace("з", "z")
    .replace("и", "i")
    .replace("к", "k")
    .replace("л", "l")
    .replace("м", "m")
    .replace("н", "n")
    .replace("о", "o")
    .replace("п", "p")
    .replace("р", "r")
    .replace("с", "s")
    .replace("т", "t")
    .replace("у", "u")
    .replace("ф", "f")
    .replace("х", "h")
    .replace("ц", "c")
    .replace("ч", "ch")
    .replace("ш", "sh")
    .replace("щ", "tch")
    .replace("ъ", "x")
    .replace("ы", "i")
    .replace("ь", "x")
    .replace("э", "e")
    .replace("ю", "yu")
    .replace("я", "ya")
    .replace("й", "y")
