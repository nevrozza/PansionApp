package allGroupMarks

import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import allGroupMarks.AllGroupMarksStore.Intent
import allGroupMarks.AllGroupMarksStore.Label
import allGroupMarks.AllGroupMarksStore.State
import allGroupMarks.AllGroupMarksStore.Message
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import lessonReport.LessonReportStore

class AllGroupMarksExecutor(
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val stupsDialogComponent: CAlertDialogComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> fetchMarks()
            is Intent.OpenDetailedStups -> scope.launch {
                stupsDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                dispatch(
                    Message.DetailedStupsOpened(intent.studentLogin)
                )
            }
        }
    }

    private fun fetchMarks() {
        scope.launch {
            nInterface.nStartLoading()
            try {
                val students = journalRepository.fetchAllGroupMarks(
                    state().groupId,
                    subjectId = state().subjectId
                ).students
                dispatch(Message.StudentsUpdated(students))
                nInterface.nSuccess()
            } catch (_: Throwable) {
                nInterface.nError("Что-то пошло не так =/") {
                    fetchMarks()
                }
            }
        }
    }


}
