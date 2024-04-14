package allGroupMarks

import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import allGroupMarks.AllGroupMarksStore.Intent
import allGroupMarks.AllGroupMarksStore.Label
import allGroupMarks.AllGroupMarksStore.State
import allGroupMarks.AllGroupMarksStore.Message
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch

class AllGroupMarksExecutor(
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> fetchMarks()
        }
    }
    private fun fetchMarks() {
        scope.launch {
            nInterface.nStartLoading()
            try {
                val students = journalRepository.fetchAllGroupMarks(state().groupId, subjectId = state().subjectId).students
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
