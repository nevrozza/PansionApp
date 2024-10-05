package dnevnikRuMarks

import allGroupMarks.AllGroupMarksStore
import allGroupMarks.AllGroupMarksStore.Message
import allGroupMarks.DatesFilter
import com.arkivanov.mvikotlin.core.store.Store
import components.MarkTableItem
import dnevnikRuMarks.DnevnikRuMarkStore.Intent
import dnevnikRuMarks.DnevnikRuMarkStore.Label
import dnevnikRuMarks.DnevnikRuMarkStore.State
import kotlinx.serialization.Serializable
import report.DnevnikRuMarksSubject
import report.ServerRatingUnit
import report.UserMark
import server.getWeekDays

interface DnevnikRuMarkStore : Store<Intent, State, Label> {
    data class State(
        val studentLogin: String,
        val subjects: HashMap<Int, List<DnevnikRuMarksSubject>> = hashMapOf(),
        val isQuarters: Boolean? = null,
        val tabIndex: Int? = null,
        val tabsCount: Int = 0,

        val pickedSubjectId: Int = 0,

        val isTableView: Boolean = false,
        val isWeekDays: Boolean = false,
        val weekDays: List<String> = getWeekDays(),

        val tableSubjects: List<DnevnikRuMarksSubject> = emptyList(),
        val mDates: List<String> = emptyList(),
        val mDateMarks: Map<String, List<MarkTableItem>> = emptyMap()
    )

    sealed interface Intent {
        data object Init: Intent

        data object OpenWeek : Intent


        data class ClickOnTab(val index: Int) : Intent

        data class ClickOnStupsSubject(val id: Int) : Intent

        data class ChangeTableView(val isTableView: Boolean) : Intent
    }

    sealed interface Message {
        data class SubjectsUpdated(val subjects: List<DnevnikRuMarksSubject>) : Message
        data class IsQuartersInited(val isQuarters: Boolean, val tabIndex: Int, val tabsCount: Int) : Message
        data class OnTabClicked(val index: Int) : Message
        data class OnStupsSubjectClicked(val id: Int) : Message

        data class MarksTableUpdated(
            val tableSubjects: List<DnevnikRuMarksSubject>,
            val mDates: List<String>,
            val mDateMarks: Map<String, List<MarkTableItem>>
        ) : Message

        data class TableViewChanged(val isTableView: Boolean) : Message
        data object WeekOpened : Message
    }

    sealed interface Label

}

