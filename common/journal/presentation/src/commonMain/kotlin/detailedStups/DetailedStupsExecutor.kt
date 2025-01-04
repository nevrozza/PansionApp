package detailedStups

import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import detailedStups.DetailedStupsStore.Intent
import detailedStups.DetailedStupsStore.Label
import detailedStups.DetailedStupsStore.State
import detailedStups.DetailedStupsStore.Message
import dnevnikRuMarks.DnevnikRuMarkStore
import kotlinx.coroutines.launch

class DetailedStupsExecutor(
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
            Intent.ChangeReason -> dispatch(Message.ReasonChanged)
        }
    }
    private fun init() {
        scope.launch {
            nInterface.nStartLoading()
            try {
//                val subjects = journalRepository.fetchDnevnikRuMarks(state().studentLogin, getQuartersNum()).subjects
//                dispatch(DnevnikRuMarkStore.Message.SubjectsUpdated(subjects))
//                journalRepository.fe
                val subjects = journalRepository.fetchAllStups(state().login).stups
            
                dispatch(Message.SubjectsUpdated(subjects))
                nInterface.nSuccess()
            } catch (x: Throwable) {
                nInterface.nError("Не удалось загрузить список ступеней", x) {
                    init()
                }
            }
        }
    }
}
