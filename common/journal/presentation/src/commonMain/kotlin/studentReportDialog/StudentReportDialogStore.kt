package studentReportDialog

import com.arkivanov.mvikotlin.core.store.Store
import report.ClientStudentLine
import report.ReportHeader
import report.StudentReportInfo
import report.UserMark
import report.UserMarkPlus
import studentReportDialog.StudentReportDialogStore.Intent
import studentReportDialog.StudentReportDialogStore.Label
import studentReportDialog.StudentReportDialogStore.State

interface StudentReportDialogStore : Store<Intent, State, Label> {
    data class State(
        val marks: List<UserMarkPlus> = emptyList(),
        val stups: List<UserMarkPlus> = emptyList(),
        val studentLine: ClientStudentLine? = null,
        val info: StudentReportInfo? = null,
        val homeTasks: List<String> = emptyList()
    )

    sealed interface Intent {
        data class OpenDialog(val login: String, val reportId: Int) : Intent
        data object CloseDialog : Intent
    }

    sealed interface Message {
        data class DialogOpened(
            val marks: List<UserMarkPlus>,
            val stups: List<UserMarkPlus>,
            val studentLine: ClientStudentLine,
            val info: StudentReportInfo,
            val homeTasks: List<String>
        ) : Message

        data object DialogClosed : Message
    }

    sealed interface Label

}
