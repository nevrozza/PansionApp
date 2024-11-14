package allGroupMarks

import ReportData
import com.arkivanov.mvikotlin.core.store.Store
import allGroupMarks.AllGroupMarksStore.Intent
import allGroupMarks.AllGroupMarksStore.Label
import allGroupMarks.AllGroupMarksStore.State
import lessonReport.LessonReportStore
import report.AllGroupMarksStudent
import report.UserMark
import server.getPreviousWeekDays
import server.getWeekDays

data class DateModule(
    val date: String,
    val module: String
)

sealed interface DatesFilter {
    data object Week: DatesFilter
    data object PreviousWeek: DatesFilter
    data class Module(val modules: List<String>): DatesFilter
}

interface AllGroupMarksStore : Store<Intent, State, Label> {
    data class State(
        val groupId: Int,
        val subjectId: Int,
        val subjectName: String,
        val groupName: String,
        val isModer: Boolean,
        val students: List<AllGroupMarksStudent> = emptyList(),
        val firstHalfNums: List<Int> = emptyList(),
        val detailedStupsLogin: String = "",
        val reportData: ReportData? = null,
        val login: String,
        val isTableView: Boolean,
        val dates: List<DateModule> = emptyList(),
        val modules: List<String> = emptyList(),

        val dateFilter: DatesFilter = DatesFilter.Week,

        val weekDays: List<String> = getWeekDays(),
        val previousWeekDays: List<String> = getPreviousWeekDays(),
    )

    sealed interface Intent {
        data object Init: Intent

        data class ChangeFilterDate(val dateFilter: DatesFilter) : Intent

        data class ChangeTableView(val isOpened: Boolean): Intent

        data class OpenDetailedStups(val studentLogin: String) : Intent

        data class OpenFullReport(val reportId: Int) : Intent

        data object DeleteReport: Intent
    }

    sealed interface Message {
        data class TableViewChanged(val isOpened: Boolean): Message

        data class FilterDateChanged(val dateFilter: DatesFilter) : Message

        data class StudentsUpdated(val students: List<AllGroupMarksStudent>, val firstHalfNums: List<Int>, val dates: List<DateModule>, val modules: List<String>) : Message
        data class DetailedStupsOpened(val login: String) : Message
        data class FullReportOpened(val reportData: ReportData?) : Message
    }

    sealed interface Label

}
