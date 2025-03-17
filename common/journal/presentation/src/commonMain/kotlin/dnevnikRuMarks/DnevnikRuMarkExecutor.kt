package dnevnikRuMarks

import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.MarkTableItem
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import dnevnikRuMarks.DnevnikRuMarkStore.Intent
import dnevnikRuMarks.DnevnikRuMarkStore.Label
import dnevnikRuMarks.DnevnikRuMarkStore.Message
import dnevnikRuMarks.DnevnikRuMarkStore.State
import report.DnevnikRuMarksSubject
import report.RFetchDnevnikRuMarksReceive
import server.sortedDate
import studentReportDialog.StudentReportComponent
import studentReportDialog.StudentReportDialogStore

class DnevnikRuMarkExecutor(
    private val journalRepository: JournalRepository = Inject.instance(),
    private val nInterface: NetworkInterface,
    private val stupsDialogComponent: CAlertDialogComponent,
    private val studentReportDialog: StudentReportComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeAction(action: Unit) {
        init()
    }

    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> {
                init()
            }

            is Intent.ClickOnTab -> {
                dispatch(Message.OnTabClicked(intent.index))
                fetchSubjects()
            }

            is Intent.ClickOnStupsSubject -> {
                    stupsDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                    dispatch(
                        Message.OnStupsSubjectClicked(intent.id)
                    )
            }

            is Intent.ChangeTableView -> dispatch(Message.TableViewChanged(intent.isTableView))

            Intent.OpenWeek -> {
                dispatch(Message.WeekOpened)
                updateMarkTable()
            }

            Intent.OpenPreviousWeek -> {
                dispatch(Message.PreviousWeekOpened)
                updateMarkTable()
            }
        }
    }

    private fun updateMarkTable() {
        val subjects: List<DnevnikRuMarksSubject> =
            if (!(state().isWeekDays || state().isPreviousWeekDays)) {
                state().subjects[(state().tabIndex ?: 0)] ?: listOf<DnevnikRuMarksSubject>()
            } else state().subjects.flatMap { it.value }
        val dates =
            subjects.flatMap {
                ((it.marks + it.stups).filter {
                    if (state().isPreviousWeekDays) {
                        it.date in state().previousWeekDays
                    } else if (state().isWeekDays) {
                        it.date in state().weekDays
                    } else {
                        true
                    }
                }.map { it.date }
                        + it.nki.filter {
                    if (state().isPreviousWeekDays) {
                        it.date in state().previousWeekDays
                    } else if (state().isWeekDays) {
                        it.date in state().weekDays
                    } else {
                        true
                    }
                }.map { it.date }
                        ).toSet()
            }.toSet().toList().sortedDate()
        val dm: MutableMap<String, MutableList<MarkTableItem>> = mutableMapOf()
        dates.forEach { d ->
            subjects.forEach { s ->
                val nd = (dm[d] ?: mutableListOf())
                nd.addAll((s.marks + s.stups).filter { it.date == d }.map {
                    MarkTableItem(
                        content = it.content,
                        login = s.subjectId.toString(),
                        reason = it.reason,
                        reportId = it.reportId,
                        module = it.module,
                        date = it.date,
                        onClick = { reportId ->
                            studentReportDialog.onEvent(
                                StudentReportDialogStore.Intent.OpenDialog(
                                    login = state().studentLogin,
                                    reportId = reportId
                                )
                            )
                        }
                    )
                })
                dm[d] = nd
            }
        }
        dispatch(
            Message.MarksTableUpdated(
                tableSubjects = subjects,
                mDates = dates,
                mDateMarks = dm
            )
        )
    }

    private fun fetchSubjects() {
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                val subjects = journalRepository.fetchDnevnikRuMarks(
                    RFetchDnevnikRuMarksReceive(
                        login = state().studentLogin,
                        quartersNum = state().tabIndex!!.toString(),
                        isQuarters = state().isQuarters!!,
                        edYear = state().edYear
                    )
                ).subjects
                withMain {
                    dispatch(Message.SubjectsUpdated(subjects))
                    nInterface.nSuccess()
                    updateMarkTable()
                }
            } catch (e: Throwable) {
                nInterface.nError("Не удалось загрузить список оценок", e) {
                    fetchSubjects()
                }
            }
        }
    }

    private fun init() {
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                val isQuarters = journalRepository.fetchIsQuarter(state().studentLogin)
                val tabsCount = if (isQuarters.isQuarters) isQuarters.num else 2
                withMain {
                    dispatch(
                        Message.IsQuartersInited(
                            isQuarters = isQuarters.isQuarters,
                            tabIndex = isQuarters.currentIndex,
                            tabsCount = tabsCount
                        )
                    )
                    fetchSubjects()
                }
            } catch (e: Throwable) {
//                        dispatch(LessonReportStore.Message.isFABShowing(true))
                nInterface.nError("Не удалось загрузить список оценок", e) {
                    init()
                }
            }
        }
    }

//    private fun getQuartersNum(): String {
//        return if (state().isQuarters!!) (state().tabIndex!!).toString() else when(state().tabIndex!!) {
//            1 -> "12"
//            2 -> "34"
//            else -> "12"
//        }
//    }
}
