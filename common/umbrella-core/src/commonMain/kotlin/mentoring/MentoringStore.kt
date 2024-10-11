package mentoring

import MentorPerson
import Person
import admin.groups.forms.CutedGroupViaSubject
import allGroupMarks.AllGroupMarksStore
import allGroupMarks.AllGroupMarksStore.Message
import allGroupMarks.DateModule
import allGroupMarks.DatesFilter
import com.arkivanov.mvikotlin.core.store.Store
import components.MarkTableItem
import home.HomeStore
import mentoring.MentoringStore.Intent
import mentoring.MentoringStore.Label
import mentoring.MentoringStore.State
import mentoring.preAttendance.ClientPreAttendance
import mentoring.preAttendance.ScheduleForAttendance
import registration.RegistrationRequest
import report.DnevnikRuMarksSubject
import report.StudentNka
import root.RootComponent
import server.getCurrentDate
import server.getDates
import server.getPreviousWeekDays
import server.getWeekDays


//val id: Int,
//    val start: String,
//    val end: String,
//    val reason: String,
//    val type: String

interface MentoringStore : Store<Intent, State, Label> {
    data class State(
        val forms: List<MentorForms> = emptyList(),
        val students: List<MentorPerson> = emptyList(),
        val requests: List<RegistrationRequest> = emptyList(),
        val preAttendance: Map<String/*Login*/, Map<String/*Date*/, ClientPreAttendance?>> = emptyMap(),
        val schedule: Map<String/*Login*/, Map<String/*Date*/, List<ScheduleForAttendance>>> = emptyMap(),
        val chosenLogin: String? = null,
        val chosenAttendanceLogin: String? = null,
        val dates: List<Pair<Int, String>> = getDates(0, 7),
        val currentDate: Pair<Int, String> = getCurrentDate(),
        val cStart: String? = null,
        val cEnd: String? = null,
        val cReason: String? = null,
        val cIsGood: Boolean? = null,

        val allGroups: List<CutedGroupViaSubject> = emptyList(),
        val chosenSubject: Int = 1,

        val isTableView: Boolean = false,
        val formsForSummary: List<Int> = emptyList(),
        val weekDays: List<String> = getWeekDays(),
        val previousWeekDays: List<String> = getPreviousWeekDays(),
        val dateFilter: DatesFilter = DatesFilter.Week,

        val allSubjects: Map<Int, String> = emptyMap(),
        val allDates: List<DateModule> = emptyList(),
        val allDateMarks: Map<String, List<MarkTableItem>> = emptyMap(),
        val allNki: Map<String, List<StudentNka>> = emptyMap(),
        val studentToGroups: Map<String, List<Int>> = emptyMap(),
        val modules: List<String> = emptyList(),

        val filteredNki: Map<String, List<StudentNka>> = emptyMap(),
        val filteredDateMarks: Map<String, List<MarkTableItem>> = emptyMap(),
        val filteredSubjects: Map<Int, String> = emptyMap(),
        val filteredDates: List<DateModule> = emptyList(),
        val filteredStudents: List<MentorPerson> = emptyList()
    )

    sealed interface Intent {
        data object FetchStudents : Intent

        data class ChangeFilterDate(val dateFilter: DatesFilter) : Intent
        data class ChangeSubject(val subjectId: Int) : Intent
        data class SolveRequest(val isAccepted: Boolean, val r: RegistrationRequest) : Intent
        data class ManageQr(val formId: Int, val isOpen: Boolean) : Intent
        data class SelectStudent(val login: String?) : Intent
        data class SelectPreAttendanceLogin(val login: String?, val date: String) : Intent
        data class ChangeDate(val date: Pair<Int, String>) : Intent

        data class StartEditPreAttendance(
            val start: String?,
            val end: String?,
            val reason: String?,
            val cIsGood: Boolean?
        ) : Intent

        data class ChangeCStart(val start: String) : Intent
        data class ChangeCEnd(val end: String) : Intent
        data class ChangeCReason(val reason: String) : Intent
        data class ChangeCIsGood(val isGood: Boolean) : Intent

        data class SavePreAttendance(val login: String, val date: String) : Intent

        data class FormToSummary(val formId: Int) : Intent
        data object ChangeView : Intent
    }

    sealed interface Message {
        data class FormsToSummaryUpdated(val formsToSummary: List<Int>) : Message

        data class FormsUpdated(val forms: List<MentorForms>) : Message

        data class StudentsFetched(
            val forms: List<MentorForms>,
            val students: List<MentorPerson>,
            val requests: List<RegistrationRequest>
        ) :
            Message

        data class StudentSelected(val login: String?) : Message
        data class PreAttendanceLoginChanged(val login: String?) : Message
        data class PreAttendanceUpdate(
            val schedule: Map<String, Map<String, List<ScheduleForAttendance>>>,
            val preAttendance: Map<String/*Login*/, Map<String/*Date*/, ClientPreAttendance?>>
        ) :
            Message

        data class DateChanged(val date: Pair<Int, String>) : Message

        data class EditPreAttendanceStarted(
            val start: String?,
            val end: String?,
            val reason: String?,
            val cIsGood: Boolean?
        ) : Message

        data class CStartChanged(val start: String) : Message
        data class CEndChanged(val end: String) : Message
        data class CReasonChanged(val reason: String) : Message
        data class CIsGoodChanged(val isGood: Boolean) : Message
        data class ViewChanged(val isTableView: Boolean) : Message
        data class TableLoaded(
            val allSubjects: Map<Int, String>,
            val allDates: List<DateModule>,
            val allDateMarks: Map<String, List<MarkTableItem>>,
            val allNki: Map<String, List<StudentNka>>,
            val chosenSubject: Int,
            val groups: List<CutedGroupViaSubject>,
            val modules: List<String>,
            val studentToGroups: Map<String, List<Int>>
        ) : Message

        data class UpdateTableAfterSubject(
            val filteredDateMarks: Map<String, List<MarkTableItem>>,
            val filteredNki: Map<String, List<StudentNka>>,
            val filteredStudents: List<MentorPerson>
        ) : Message

        data class FilterDateChanged(val dateFilter: DatesFilter) : Message
        data class UpdateTableAfterPeriod(
            val filteredSubjects: Map<Int, String>,
            val filteredDates: List<DateModule>
        ) : Message

        data class SubjectChanged(val subjectId: Int) : Message
    }

    sealed interface Label

}
