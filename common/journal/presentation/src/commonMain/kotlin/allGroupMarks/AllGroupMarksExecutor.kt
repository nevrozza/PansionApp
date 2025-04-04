package allGroupMarks

import JournalRepository
import allGroupMarks.AllGroupMarksStore.Intent
import allGroupMarks.AllGroupMarksStore.Label
import allGroupMarks.AllGroupMarksStore.Message
import allGroupMarks.AllGroupMarksStore.State
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import report.RFetchAllGroupMarksReceive

class AllGroupMarksExecutor(
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository = Inject.instance(),
    private val stupsDialogComponent: CAlertDialogComponent,
    private val nOpenReportInterface: NetworkInterface
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeAction(action: Unit) {
        fetchMarks()
    }

    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> fetchMarks()
            is Intent.OpenDetailedStups -> {
                stupsDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                dispatch(
                    Message.DetailedStupsOpened(intent.studentLogin)
                )
            }

            is Intent.OpenFullReport -> openFullReport(intent.reportId)
            Intent.DeleteReport -> dispatch(Message.FullReportOpened(null))
            is Intent.ChangeTableView -> dispatch(Message.TableViewChanged(intent.isOpened))
            is Intent.ChangeFilterDate -> {
                dispatch(
                    Message.FilterDateChanged(
                        getDF(
                            oldDF = state().dateFilter,
                            newDF = intent.dateFilter
                        )
                    )
                )
            }
        }
    }

    private fun openFullReport(reportId: Int) {
        scope.launchIO {
            nOpenReportInterface.nStartLoading()
            try {
                val reportData = journalRepository.fetchFullReportData(reportId)
                withMain {
                    nOpenReportInterface.nSuccess()
                    dispatch(Message.FullReportOpened(reportData))
                }
            } catch (e: Throwable) {
                nOpenReportInterface.nError("Что-то пошло не так =/", e) {
                    nOpenReportInterface.nSuccess()
                }
            }
        }
    }

    private fun fetchMarks() {
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                val r = journalRepository.fetchAllGroupMarks(
                    RFetchAllGroupMarksReceive(
                        groupId = state().groupId,
                        subjectId = state().subjectId,
                        edYear = state().edYear
                    )
                )
                val dates =
                    (r.students.flatMap {
                        (it.stups.map { DateModule(it.mark.date, it.mark.module) }
                                + it.marks.map { DateModule(it.mark.date, it.mark.module) }
                            + it.nki.map { DateModule(it.date, it.module) }
                        ).toSet() })
                withMain {
                    dispatch(
                        Message.StudentsUpdated(
                            r.students,
                            r.firstHalfNums,
                            dates,
                            modules = dates.map { it.module }.toSet().toList()
                        )
                    )
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                nInterface.nError("Что-то пошло не так =/", e) {
                    fetchMarks()
                }
            }
        }
    }


}

fun getDF(oldDF: DatesFilter, newDF: DatesFilter) : DatesFilter {
    return if(newDF is DatesFilter.PreviousWeek) {
      DatesFilter.PreviousWeek
    } else if (newDF is DatesFilter.Week) {
        DatesFilter.Week
    } else if (oldDF in listOf(DatesFilter.Week, DatesFilter.PreviousWeek)) {
        newDF
    } else {
        val prevMods = (oldDF as DatesFilter.Module).modules
        val newMod = (newDF as DatesFilter.Module).modules.first()
        val new = if (newMod in prevMods) prevMods - newMod else prevMods + newMod
        DatesFilter.Module(new)
    }
}
