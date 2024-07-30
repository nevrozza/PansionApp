package mentoring

import MentorPerson
import Person
import com.arkivanov.mvikotlin.core.store.Store
import home.HomeStore
import mentoring.MentoringStore.Intent
import mentoring.MentoringStore.Label
import mentoring.MentoringStore.State
import mentoring.preAttendance.ClientPreAttendance
import mentoring.preAttendance.ScheduleForAttendance
import root.RootComponent
import server.getCurrentDate
import server.getDates


//val id: Int,
//    val start: String,
//    val end: String,
//    val reason: String,
//    val type: String

interface MentoringStore : Store<Intent, State, Label> {
    data class State(
        val forms: List<MentorForms> = emptyList(),
        val students: List<MentorPerson> = emptyList(),
        val preAttendance: Map<String/*Login*/, Map<String/*Date*/, ClientPreAttendance?>> = emptyMap(),
        val schedule: Map<String/*Login*/, Map<String/*Date*/, List<ScheduleForAttendance>>> = emptyMap(),
        val chosenLogin: String? = null,
        val chosenAttendanceLogin: String? = null,
        val dates: List<Pair<Int, String>> = getDates(0, 7),
        val currentDate: Pair<Int, String> = getCurrentDate(),

        val cStart: String? = null,
        val cEnd: String? = null,
        val cReason: String? = null,
        val cIsGood: Boolean? = null
    )

    sealed interface Intent {
        data object FetchStudents : Intent
        data class SelectStudent(val login: String?) : Intent
        data class SelectPreAttendanceLogin(val login: String?, val date: String) : Intent
        data class ChangeDate(val date: Pair<Int, String>) : Intent

        data class StartEditPreAttendance(val start: String?, val end: String?, val reason: String?, val cIsGood: Boolean?) : Intent
        data class ChangeCStart(val start: String) : Intent
        data class ChangeCEnd(val end: String) : Intent
        data class ChangeCReason(val reason: String) : Intent
        data class ChangeCIsGood(val isGood: Boolean) : Intent


        data class SavePreAttendance(val login: String, val date: String) : Intent
    }

    sealed interface Message {
        data class StudentsFetched(val forms: List<MentorForms>, val students: List<MentorPerson>) :
            Message

        data class StudentSelected(val login: String?) : Message
        data class PreAttendanceLoginChanged(val login: String?) : Message
        data class PreAttendanceUpdate(val schedule: Map<String, Map<String, List<ScheduleForAttendance>>>, val preAttendance: Map<String/*Login*/, Map<String/*Date*/, ClientPreAttendance?>>) :
            Message
        data class DateChanged(val date: Pair<Int, String>) : Message

        data class EditPreAttendanceStarted(val start: String?, val end: String?, val reason: String?, val cIsGood: Boolean?) : Message
        data class CStartChanged(val start: String) : Message
        data class CEndChanged(val end: String) : Message
        data class CReasonChanged(val reason: String) : Message
        data class CIsGoodChanged(val isGood: Boolean) : Message
    }

    sealed interface Label

}
