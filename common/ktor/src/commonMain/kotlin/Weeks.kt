import admin.calendar.Holiday
import kotlinx.datetime.*
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.char
import kotlinx.serialization.Serializable
import server.*


@Serializable
data class Week(
    val num: Int,
    val dates: List<String>
)

fun getWeeks(
    holidays: List<Holiday>,
    edYear: Int = getCurrentEdYear(),
    isWhole: Boolean = false
) : List<Week> {
    val vacations = holidays.flatMap { getDateRange(it.start, it.end) }


    val startDate = LocalDate.parse("${edYear}-09-01")
    val endDate =
        if (isWhole) LocalDate.parse("${edYear + 1}-09-01")
        else Clock.System.now().toLocalDateTime(appTimeZone).date


    val weeks = mutableListOf<Week>()

    var currentDate = startDate
    var weekNum = 1

    while (currentDate <= endDate) {
        val firstWeekDay = currentDate.daysShift(-DayOfWeek.values().indexOf(currentDate.dayOfWeek))
        if (firstWeekDay.toEpochDays() >= startDate.toEpochDays()) {
            var weekDates = mutableListOf<String>()
            for (i in 0 until DayOfWeek.values().count()) {
                weekDates.add(firstWeekDay.daysShift(i).to10())
            }

            weekDates = weekDates.filter {
                it !in vacations
            }.toMutableList()
            if (weekDates.isNotEmpty()) {
                weeks.add(Week(weekNum, weekDates))
                weekNum++
            }
        }

        // Переходим к следующей неделе
        currentDate = firstWeekDay.daysShift(7)
    }
    return weeks
}


fun getDateRange(startStr: String, endStr: String): List<String> {
    // Форматируем строки в объекты LocalDate
    val formatter = LocalDate.Format {
        dayOfMonth(); char('.'); monthNumber();char('.');year();
    }
    val startDate = LocalDate.parse(startStr, formatter)
    val endDate = LocalDate.parse(endStr, formatter)

    // Создаем список дат между startDate и endDate включительно
    val dates = mutableListOf<String>()
    var currentDate = startDate
    while (currentDate <= endDate) {
        dates.add(currentDate.to10())
        currentDate = currentDate.daysShift(1)
    }

    return dates
}
