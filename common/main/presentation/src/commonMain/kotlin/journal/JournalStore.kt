package journal

import Person
import ReportData
import com.arkivanov.mvikotlin.core.store.Store
import journal.JournalStore.Intent
import journal.JournalStore.Label
import journal.JournalStore.State
import journal.init.TeacherGroup
import report.ReportHeader
import server.getSixTime

interface JournalStore : Store<Intent, State, Label> {
    data class State(
        val isMentor: Boolean,
        val childrenGroupIds: List<Int> = emptyList(),
        val studentsInGroup: List<Person> = emptyList(),
        val currentGroupId: Int = 0,
        val teacherGroups: List<TeacherGroup> = emptyList(),
        val headers: List<ReportHeader> = emptyList(),
        val creatingReportId: Int = -1,
        val openingReportData: ReportData? = null,
        val time: String = getSixTime(),
        val currentModule: String = "",
        val filterTeacherLogin: String? = null,
        val filterGroupId: Int? = null,
        val filterDate: String? = null,
        val filterStatus: Boolean? = null,
        val filterMyChildren: Boolean = isMentor
    )

    sealed interface Intent {
        data object Init : Intent
        data class OnGroupClicked(val groupId: Int, val time: String) : Intent

        data object Refresh : Intent

        data object CreateReport : Intent

        data class FetchReportData(val reportHeader: ReportHeader) : Intent
        data object ResetCreatingId : Intent
        data object ResetReportData : Intent
        data object ResetTime : Intent


        data class FilterTeacher(val teacherLogin: String?) : Intent
        data class FilterGroup(val groupId: Int?) : Intent
        data class FilterDate(val date: String?) : Intent
        data class FilterStatus(val bool: Boolean?) : Intent
        data class FilterMyChildren(val bool: Boolean) : Intent
    }

    sealed interface Message {
        data class StudentsInGroupUpdated(val students: List<Person>, val groupId: Int) : Message
        data class HeadersUpdated(val headers: List<ReportHeader>, val currentModule: String) : Message
        data class TeacherGroupsUpdated(val teacherGroups: List<TeacherGroup>) : Message
        data class ReportCreated(val id: Int) : Message

        data object CreatingIdReseted : Message

        data class ReportDataFetched(val reportData: ReportData) : Message
        data object ReportDataReseted : Message

        data class TimeChanged(val time: String) : Message

        data class TeacherFiltered(val teacherLogin: String?) : Message
        data class GroupFiltered(val groupId: Int?) : Message
        data class DateFiltered(val date: String?) : Message
        data class StatusFiltered(val bool: Boolean?) : Message
        data class MyChildrenFiltered(val bool: Boolean) : Message
        data class MyChildrenGroupsFetched(val ids: List<Int>) : Message
    }

    sealed interface Label

}
