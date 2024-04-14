package journal

import Person
import ReportData
import com.arkivanov.mvikotlin.core.store.Store
import journal.JournalStore.Intent
import journal.JournalStore.Label
import journal.JournalStore.State
import journal.init.TeacherGroup
import report.ReportHeader

interface JournalStore : Store<Intent, State, Label> {
    data class State(
        val studentsInGroup: List<Person> = emptyList(),
        val currentGroupId: Int = 0,
        val teacherGroups: List<TeacherGroup> = emptyList(),
        val headers: List<ReportHeader> = emptyList(),
        val creatingReportId: Int = -1,
        val openingReportData: ReportData? = null
    )

    sealed interface Intent {
        data object Init : Intent
        data class OnGroupClicked(val groupId: Int) : Intent

        data object Refresh : Intent

        data object CreateReport : Intent

        data class FetchReportData(val reportHeader: ReportHeader) : Intent
        data object ResetCreatingId : Intent
        data object ResetReportData : Intent
    }

    sealed interface Message {
        data class StudentsInGroupUpdated(val students: List<Person>, val groupId: Int) : Message
        data class HeadersUpdated(val headers: List<ReportHeader>) : Message
        data class TeacherGroupsUpdated(val teacherGroups: List<TeacherGroup>) : Message
        data class ReportCreated(val id: Int) : Message

        data object CreatingIdReseted : Message

        data class ReportDataFetched(val reportData: ReportData) : Message
        data object ReportDataReseted : Message
    }

    sealed interface Label

}
