package server

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn


fun String.toMinutes(): Int {
    val parts = this.split(":")
    return parts[0].toInt() * 60 + parts[1].toInt()
}

fun List<String>.sortedDate() = this.sortedBy { getLocalDate(it).toEpochDays() }


fun getLocalDate(date: String): LocalDate {
    val s = date.replace(".", "")
    val day = s.substring(0, 2).toInt()
    val month = s.substring(2, 4).toInt()
    val year = s.substring(4).toInt()
    return LocalDate(
        year = year,
        monthNumber = month,
        dayOfMonth = day
    )
}

fun getCurrentDayTime() : String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3"))
    return "${today.time.hour.twoNums()}:${today.time.minute.twoNums()}"
}

fun isTimeFormat(str: String): Boolean {
    val pattern = """\b([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]\b""".toRegex()
    val parts = str.split("-")

    val isNums = parts[0].replaceFirst(":", "").toIntOrNull() != null &&
            parts[1].replaceFirst(":", "").toIntOrNull() != null
    println("${parts[0].replaceFirst(":", "")} ${parts[1].replaceFirst(":", "").toIntOrNull()}")
    if (isNums) {
        val start = parts[0].toMinutes()
        val end = parts[1].toMinutes()
        return (pattern.matches(str) && start < end)
    } else return false
}
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

fun getPreviousWeekDays(): List<String> {
    val today = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3")).date
    val days = mutableListOf<LocalDate>()
    val firstWeekDay = today.daysShift(-DayOfWeek.entries.indexOf(today.dayOfWeek) - 7) // Calculate the first day of the previous week

    for (i in 0 until 7) { // Iterate for 7 days for the previous week
        days.add(firstWeekDay.daysShift(i))
    }

    return days.map { time ->
        "${time.dayOfMonth.twoNums()}." +
                "${time.monthNumber.twoNums()}." +
                "${time.year}"
    }
}

fun fetchReason(reasonId: String): String {
    return if(reasonId.subSequence(0,3) == "!ds") {
        fetchTitle(reasonId)
    } else {
        return when (reasonId.subSequence(0, 3)) {
            "!dz" -> "ДЗ"
            "!cl" -> "Кл/Р"
            "!st" -> "Ступени"
            "!ds" -> "Дисциплина"
            else -> "null"
        } + ": " + fetchTitle(reasonId)
    }
}

fun fetchTitle(reasonId: String): String {
    return when (reasonId.subSequence(0, 3)) {
        "!dz" -> {
            when (reasonId.last()) {
                '1' -> "Тест"
                '2' -> "Письм. работа"
                '3' -> "Решение задач"
                '4' -> "Устный ответ"
                else -> "null"
            }
        }

        "!cl" -> {
            when (reasonId.last()) {
                '1' -> "К/Р"
                '2' -> "С/Р"
                '3' -> "Тест"
                '4' -> "Письм. работа"
                '5' -> "Работа на уроке"
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
        minus(1, DateTimeUnit.DayBased(-days))
    }
    days > 0 -> {
        plus(1, DateTimeUnit.DayBased(days))
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
    if (this.isNaN()) {
        return Float.NaN
    } else {
        //wasm fix... why(
        val strFirst = this.toString()
        val cutLength = strFirst.split(".")[0].length+1+ numFractionDigits
        var str = strFirst.cut(cutLength)
        if (strFirst.cut(cutLength+1).last().toString().toInt() >= 5) {
            str = strFirst.cut(cutLength-1) + (str.last().toString().toInt()+1)
        }
        return str.toFloat()
//        val factor = 10.0.pow(numFractionDigits.toDouble())
//        return ((this * factor).roundToInt() / factor).toFloat()
    }
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

fun getCurrentDate(): Pair<Int, String> {
    val today = Clock.System.todayIn(TimeZone.of("UTC+3"))
    val dayOfWeek = when (today.dayOfWeek) {
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 7
        else -> 1
    }
    return Pair(
        dayOfWeek,
        "${today.dayOfMonth.twoNums()}.${today.monthNumber.twoNums()}.${today.year}"
    )

}

fun getDates(minus: Int = 0, plus: Int = 7): List<Pair<Int, String>> {
    val dates = mutableListOf<Pair<Int, String>>()
    val today = Clock.System.todayIn(TimeZone.of("UTC+3"))
    val startDate = today.minus(minus, DateTimeUnit.DAY)// сегодняшняя дата //минус 7 дней
    val endDate = today.plus(plus, DateTimeUnit.DAY) // сегодняшняя дата плюс 7 дней

    var currentDate = startDate
    while (currentDate <= endDate) {
        val dayOfWeek = when (currentDate.dayOfWeek) {
            DayOfWeek.MONDAY -> 1
            DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5
            DayOfWeek.SATURDAY -> 6
            DayOfWeek.SUNDAY -> 7
            else -> 1
        }
        dates.add(
            Pair(
                dayOfWeek,
                "${currentDate.dayOfMonth.twoNums()}.${currentDate.monthNumber.twoNums()}.${currentDate.year}"
            )
        )
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
    }
    return dates
}

