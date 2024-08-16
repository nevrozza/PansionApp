package studentLines

import CDispatcher
import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch
import report.RFetchStudentLinesReceive
import server.getLocalDate
import server.toMinutes
import studentLines.StudentLinesStore.Intent
import studentLines.StudentLinesStore.Label
import studentLines.StudentLinesStore.State
import studentLines.StudentLinesStore.Message

class StudentLinesExecutor(
    private val journalRepository: JournalRepository,
    private val nInterface: NetworkInterface,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
        }
    }

    private fun init() {
        scope.launch(CDispatcher) {
            try {
                nInterface.nStartLoading()
                val r = journalRepository.fetchStudentLines(RFetchStudentLinesReceive(login = state().login))
                scope.launch {
                    dispatch(Message.StudentLinesInited(
                        r.studentLines.sortedWith(
                            compareBy(
                                { getLocalDate(it.date).toEpochDays() },
                                { it.time.toMinutes() })
                        ).reversed()
                    ))
                    nInterface.goToNone()
                }
            } catch (_: Throwable) {
                nInterface.nError("Что-то пошло не так =(") {
                    init()
                }
            }
        }
    }
}
