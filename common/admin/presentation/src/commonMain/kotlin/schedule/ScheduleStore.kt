package schedule

import admin.cabinets.CabinetItem
import admin.schedule.ScheduleGroup
import admin.schedule.SchedulePerson
import admin.schedule.ScheduleSubject
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayAt
import kotlinx.datetime.todayIn
import schedule.ScheduleStore.Intent
import schedule.ScheduleStore.Label
import schedule.ScheduleStore.State
import server.twoNums

interface ScheduleStore : Store<Intent, State, Label> {
    data class State(
        val teachers: List<SchedulePerson> = emptyList(),
        val students: List<SchedulePerson> = emptyList(),
        val subjects: List<ScheduleSubject> = emptyList(),
        val cabinets: List<CabinetItem> = emptyList(),
        val groups: List<ScheduleGroup> = emptyList(),
        val activeTeachers: List<Pair<String, List<String>>> = emptyList(),
        val items: List<Pair<String, List<ScheduleItem>>> = emptyList(),

        val ciLogin: String? = null,
        val ciId: Int? = null,
        val ciCabinet: Int = 0,
        val ciTimings: List<ScheduleTiming>? = null,
        val ciTiming: ScheduleTiming? = null,
        val ciPreview: Boolean = false,
        val ciIsPair: Boolean = false,


        val eiState: EditState = EditState.Preview,
        val eiIndex: Int? = null,
        val eiCabinet: Int? = null,
        val eiGroupId: Int? = null,
        val eiTiming: Pair<String, String>? = null,

        val eiCabinetErrorGroupId: Int = 0,
        val eiStudentErrors: List<StudentError> = emptyList(),

        val isDefault: Boolean = false,
        val defaultDate: Int = 1,
        val dates: List<Pair<Int, String>> = getDates(),
        val currentDate: Pair<Int, String> = getCurrentDate()
    )

    sealed interface Intent {
        data object Init : Intent


        data object ChangeEditMode : Intent

        data class ChangeDefaultDate(val date: Int) : Intent
        data class ChangeCurrentDate(val date: Pair<Int, String>) : Intent


        data class StartEdit(val index: Int) : Intent

        data class eiChooseGroup(val groupId: Int) : Intent

        data class eiChangeState(val state: EditState) : Intent

        data class eiChangeTiming(val timing: Pair<String, String>) : Intent
        data class eiChangeCabinet(val cabinet: Int) : Intent


        data class eiCheck(
            val cabinet: Int,
            val login: String,
            val id: Int,
            val s: Pair<String, String>
        ) : Intent

        data class eiSave(
            val index: Int,
            val cabinet: Int,
            val login: String,
            val id: Int,
            val s: Pair<String, String>
        ) : Intent

        data class eiDelete(val index: Int) : Intent

        data class ciStart(val login: String) : Intent
        data class ciChooseGroup(val groupId: Int) : Intent

        data class ciChooseTime(val t: ScheduleTiming) : Intent
        data class ciChangeCabinet(val cabinet: Int) : Intent

        data object ciPreview : Intent

        data object ciCreate : Intent

        data object ciNullGroupId : Intent
        data object ciFalsePreview : Intent

        data object UpdateCTeacherList : Intent

        data class CreateTeacher(val login: String) : Intent


        data object ciChangeIsPair : Intent

        data object SaveSchedule : Intent
    }

    sealed interface Message {

        data class ListUpdated(val list: List<Pair<String, List<ScheduleItem>>>) : Message

        data object EditModeChanged : Message

        data class DefaultDateChanged(val defaultDate: Int) : Message
        data class CurrentDateChanged(val currentDate: Pair<Int, String>) : Message

        data class Inited(
            val teachers: List<SchedulePerson>,
            val students: List<SchedulePerson>,
            val subjects: List<ScheduleSubject>,
            val groups: List<ScheduleGroup>,
            val cabinets: List<CabinetItem>
        ) : Message

        data object ciIsPairChanged : Message

        data class EditStarted(val index: Int) : Message
        data class eiGroupChosed(val groupId: Int) : Message
        data class eiTimingChanged(val timing: Pair<String, String>) : Message
        data class eiStateChanged(val state: EditState) : Message
        data class eiCabinetChanged(val cabinet: Int) : Message
        data class eiErrorsUpdated(
            val cabinetErrorGroupId: Int,
            val studentErrors: List<StudentError>
        ) : Message


        data class ciStarted(val login: String, val cabinet: Int) : Message

        data class ciGroupChosed(val groupId: Int) : Message
        data class ciCabinetChanged(val cabinet: Int) : Message

        data class ciTimeChosed(val t: ScheduleTiming) : Message

        data object ciPreviewed : Message

        data object ciReset : Message

        data class ciTimingsGot(val timings: List<ScheduleTiming>) : Message

        data object ciGroupIdNulled : Message
        data object ciPreviewFalsed : Message

        data class TeacherCreated(val activeTeachers: List<String>) : Message
        data class TeacherListUpdated(val activeTeachers: List<Pair<String, List<String>>>) : Message
        data class ItemsUpdated(val items: List<ScheduleItem>) : Message


    }

    sealed interface Label


    sealed interface EditState {
        data object Preview : EditState
        data object Groups : EditState
        data object Timings : EditState
    }
}

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
        "${today.dayOfMonth.twoNums()}.${today.monthNumber.twoNums()}.${today.year % 100}"
    )

}

fun getDates(): List<Pair<Int, String>> {
    val dates = mutableListOf<Pair<Int, String>>()
    val today = Clock.System.todayIn(TimeZone.of("UTC+3"))
    val startDate = today// сегодняшняя дата //минус 7 дней
    val endDate = today.plus(7, DateTimeUnit.DAY) // сегодняшняя дата плюс 7 дней

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
                "${currentDate.dayOfMonth.twoNums()}.${currentDate.monthNumber.twoNums()}.${currentDate.year % 100}"
            )
        )
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
    }
    return dates
}


val weekPairs = listOf(
    Pair(1, "Пн"),
    Pair(2, "Вт"),
    Pair(3, "Ср"),
    Pair(4, "Чт"),
    Pair(5, "Пт"),
    Pair(6, "Сб"),
    Pair(7, "Вс"),
)
