package home

import FIO
import auth.RCheckConnectionResponse
import com.arkivanov.mvikotlin.core.store.Store
import home.HomeStore.Intent
import home.HomeStore.Label
import home.HomeStore.State
import journal.init.TeacherGroup
import report.Grade
import report.ReportHeader
import schedule.PersonScheduleItem
import schedule.ScheduleItem
import server.getCurrentDate
import server.getDate
import server.getDates

interface HomeStore : Store<Intent, State, Label> {
    data class State(
        val avatarId: Int,
        val login: String,
        val name: String,
        val surname: String,
        val praname: String,
        val grades: List<Grade> = emptyList(),
        val period: Period = Period.WEEK,
        val teacherGroups: List<TeacherGroup> = emptyList(),
        val averageGradePoint: HashMap<Period, Float?> = hashMapOf(
            Period.WEEK to null,
            Period.MODULE to null,
            Period.HALF_YEAR to null,
            Period.YEAR to null
        ),
        val ladderOfSuccess: HashMap<Period, Pair<Int, Int>?> = hashMapOf(
            Period.WEEK to null,
            Period.MODULE to null,
            Period.HALF_YEAR to null,
            Period.YEAR to null
        ),
        val homeWorkEmoji: String? = null,

        val items: HashMap<String, List<PersonScheduleItem>> = hashMapOf(),
        val currentDate: Pair<Int, String> = getCurrentDate(),
        val today: String = getCurrentDate().second,
        val dates: List<Pair<Int, String>> = getDates(4, 4),
        val isDatesShown: Boolean = false,

        val role: String,
        val someHeaders: List<ReportHeader> = emptyList()
    )

    sealed interface Intent {
        data object Init : Intent
        data object ChangeIsDatesShown : Intent

        data class ChangeDate(val date: Pair<Int, String>) : Intent

        data class UpdateSomeHeaders(val someHeaders: List<ReportHeader>) : Intent
        data class UpdateAvatarId(val avatarId: Int) : Intent


        data object ChangePeriod: Intent
//        data class ChangeDate()
        //val avatarId: Int,
        //                        val login: String,
        //                        val name: String,
        //                        val surname: String,
        //                        val praname: String
    }

    sealed interface Message {
        data class AvatarIdUpdated(val avatarId: Int) : Message

        data class SomeHeadersUpdated(val someHeaders: List<ReportHeader>) : Message
        data class TeacherGroupUpdated(val teacherGroups: List<TeacherGroup>): Message
        data class QuickTabUpdated(val avg: HashMap<Period, Float?>, val stups: HashMap<Period, Pair<Int, Int>?>) : Message
//        data class Inited(val avatarId: Int,
//                          val login: String,
//                          val name: String,
//                          val surname: String,
//                          val praname: String) : Message

        data class GradesUpdated(val grades: List<Grade>) : Message

        data class ItemsUpdated(val items: HashMap<String, List<PersonScheduleItem>>) : Message

        data class DateChanged(val date: Pair<Int, String>) : Message

        data object IsDatesShownChanged : Message
        data class PeriodChanged(val period: Period) : Message
    }

    sealed interface Label


    enum class Period {
        WEEK, MODULE, HALF_YEAR, YEAR
    }

    object Emojis {
        const val check: String = "✅"
        const val smileTeeth: String = "\uD83D\uDE01"
        const val smile: String = "\uD83D\uDE42"
        const val normal: String = "\uD83D\uDE10"
        const val scared: String = "\uD83D\uDE28"
        const val horror: String = "\uD83D\uDE31"
        const val death: String = "☠\uFE0F"
    }
}

