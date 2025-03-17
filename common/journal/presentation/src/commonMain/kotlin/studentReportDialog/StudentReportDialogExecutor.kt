package studentReportDialog

import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cBottomSheet.CBottomSheetComponent
import components.cBottomSheet.CBottomSheetStore
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import report.RFetchStudentReportReceive
import studentReportDialog.StudentReportDialogStore.Intent
import studentReportDialog.StudentReportDialogStore.Label
import studentReportDialog.StudentReportDialogStore.Message
import studentReportDialog.StudentReportDialogStore.State

class StudentReportDialogExecutor(
    private val journalRepository: JournalRepository = Inject.instance(),
    private val dialog: CBottomSheetComponent,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.CloseDialog -> { dispatch(Message.DialogClosed); dialog.fullySuccess() }
            is Intent.OpenDialog -> openDialog(login = intent.login, reportId = intent.reportId)
        }
    }

    private fun openDialog(login: String, reportId: Int) {
        scope.launchIO {
            try {
                withMain {
                    dialog.nInterface.nStartLoading()
                    dialog.onEvent(CBottomSheetStore.Intent.ShowSheet)
                }
                val r = journalRepository.fetchStudentReport(RFetchStudentReportReceive(login = login, reportId = reportId))
                withMain {
                    dispatch(Message.DialogOpened(marks = r.marks, stups = r.stups, studentLine = r.studentLine, info = r.info, homeTasks = r.homeTasks))
                    dialog.nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                dialog.nInterface.nError("Не удалось собрать данные", e) {
                    openDialog(login, reportId)
                }
            }
        }
    }
}
