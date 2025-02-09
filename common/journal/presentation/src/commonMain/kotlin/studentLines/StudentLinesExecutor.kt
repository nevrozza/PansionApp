package studentLines

import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import report.RFetchStudentLinesReceive
import server.getLocalDate
import server.toMinutes
import studentLines.StudentLinesStore.Intent
import studentLines.StudentLinesStore.Label
import studentLines.StudentLinesStore.Message
import studentLines.StudentLinesStore.State

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
        scope.launchIO {
            try {
                nInterface.nStartLoading()
                val r = journalRepository.fetchStudentLines(RFetchStudentLinesReceive(login = state().login, edYear = state().edYear))
                withMain {
                    dispatch(Message.StudentLinesInited(
                        r.studentLines.sortedWith(
                            compareBy(
                                { getLocalDate(it.date).toEpochDays() },
                                { it.time.toMinutes() })
                        ).reversed()
                    ))
                    nInterface.goToNone()
                }
            } catch (e: Throwable) {
                nInterface.nError("Что-то пошло не так =(", e) {
                    init()
                }
            }
        }
    }
}
