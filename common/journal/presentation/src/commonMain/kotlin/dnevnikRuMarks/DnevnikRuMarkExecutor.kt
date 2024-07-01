package dnevnikRuMarks

import CDispatcher
import JournalRepository
import allGroupMarks.AllGroupMarksStore
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.networkInterface.NetworkInterface
import dnevnikRuMarks.DnevnikRuMarkStore.Intent
import dnevnikRuMarks.DnevnikRuMarkStore.Label
import dnevnikRuMarks.DnevnikRuMarkStore.State
import dnevnikRuMarks.DnevnikRuMarkStore.Message
import kotlinx.coroutines.launch
import lessonReport.Attented
import lessonReport.AvgMark
import lessonReport.LessonReportStore
import lessonReport.Mark
import lessonReport.StudentLine
import lessonReport.Stup
import report.RFetchReportStudentsReceive

class DnevnikRuMarkExecutor(
    private val journalRepository: JournalRepository,
    private val nInterface: NetworkInterface,
    private val stupsDialogComponent: CAlertDialogComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
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
                scope.launch {
                    stupsDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                    dispatch(
                        Message.OnStupsSubjectClicked(intent.id)
                    )
                }
            }
        }
    }


    private fun fetchSubjects() {
        scope.launch {
            nInterface.nStartLoading()
            try {
                val subjects = journalRepository.fetchDnevnikRuMarks(state().studentLogin, quartersNum = state().tabIndex!!.toString(), isQuarters = state().isQuarters!!).subjects
                dispatch(Message.SubjectsUpdated(subjects))
                nInterface.nSuccess()
            } catch (_: Throwable) {
                nInterface.nError("Не удалось загрузить список оценок") {
                    fetchSubjects()
                }
            }
        }
    }

    private fun init() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                val isQuarters = journalRepository.fetchIsQuarter(state().studentLogin)
                val tabsCount = if (isQuarters.isQuarters) isQuarters.num else 2
                scope.launch {
                    dispatch(
                        Message.IsQuartersInited(
                            isQuarters = isQuarters.isQuarters,
                            tabIndex = isQuarters.currentIndex,
                            tabsCount = tabsCount
                        )
                    )
                    fetchSubjects()
                }
            } catch (_: Throwable) {
//                        dispatch(LessonReportStore.Message.isFABShowing(true))
                nInterface.nError("Не удалось загрузить список оценок") {
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
