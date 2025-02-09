package detailedStups

import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import detailedStups.DetailedStupsStore.Intent
import detailedStups.DetailedStupsStore.Label
import detailedStups.DetailedStupsStore.Message
import detailedStups.DetailedStupsStore.State
import deviceSupport.launchIO
import deviceSupport.withMain
import report.RFetchDetailedStupsReceive

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
        scope.launchIO {
            nInterface.nStartLoading()
            try {
//                val subjects = journalRepository.fetchDnevnikRuMarks(state().studentLogin, getQuartersNum()).subjects
//                dispatch(DnevnikRuMarkStore.Message.SubjectsUpdated(subjects))
//                journalRepository.fe
                val subjects = journalRepository.fetchAllStups(
                    RFetchDetailedStupsReceive(
                        login = state().login,
                        edYear = state().edYear
                    )
                ).stups
                withMain {
                    dispatch(Message.SubjectsUpdated(subjects))
                    nInterface.nSuccess()
                }
            } catch (x: Throwable) {
                nInterface.nError("Не удалось загрузить список ступеней", x) {
                    init()
                }
            }
        }
    }
}
