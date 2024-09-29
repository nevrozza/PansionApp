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
import components.networkInterface.NetworkState
import kotlinx.coroutines.launch
import lessonReport.LessonReportStore

class AllGroupMarksExecutor(
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val stupsDialogComponent: CAlertDialogComponent,
    private val nOpenReportInterface: NetworkInterface
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

            is Intent.OpenFullReport -> openFullReport(intent.reportId)
            Intent.DeleteReport -> dispatch(Message.FullReportOpened(null))
            is Intent.ChangeTableView -> dispatch(Message.TableViewChanged(intent.isOpened))
            is Intent.ChangeFilterDate -> {


                val df = if (intent.dateFilter is DatesFilter.Week) {
                    DatesFilter.Week
                } else if (state().dateFilter is DatesFilter.Week) {
                    intent.dateFilter
                } else {
                    val prevMods = (state().dateFilter as DatesFilter.Module).modules
                    val newMod = (intent.dateFilter as DatesFilter.Module).modules.first()
                    val new = if (newMod in prevMods) prevMods - newMod else prevMods + newMod
                    DatesFilter.Module(new)
                }
                dispatch(
                    Message.FilterDateChanged(
                        df
                    )
                )
            }
        }
    }

    private fun openFullReport(reportId: Int) {
        scope.launch {
            nOpenReportInterface.nStartLoading()
            try {
                val reportData = journalRepository.fetchFullReportData(reportId)
                nOpenReportInterface.nSuccess()
                dispatch(Message.FullReportOpened(reportData))
            } catch (_: Throwable) {
                nOpenReportInterface.nError("Что-то пошло не так =/") {
                    nOpenReportInterface.nSuccess()
                }
            }
        }
    }

    private fun fetchMarks() {
        scope.launch {
            nInterface.nStartLoading()
            try {
                val r = journalRepository.fetchAllGroupMarks(
                    state().groupId,
                    subjectId = state().subjectId
                )
                val dates =
                    (r.students.flatMap { (it.stups.map { DateModule(it.mark.date, it.mark.module) } + it.marks.map { DateModule(it.mark.date, it.mark.module) }).toSet() })

                dispatch(Message.StudentsUpdated(r.students, r.firstHalfNums, dates, modules = dates.map { it.module }.toSet().toList()))
                nInterface.nSuccess()
            } catch (_: Throwable) {
                nInterface.nError("Что-то пошло не так =/") {
                    fetchMarks()
                }
            }
        }
    }


}
