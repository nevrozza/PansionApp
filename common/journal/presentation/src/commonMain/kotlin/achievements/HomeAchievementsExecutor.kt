package achievements

import CDispatcher
import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import achievements.HomeAchievementsStore.Intent
import achievements.HomeAchievementsStore.Label
import achievements.HomeAchievementsStore.State
import achievements.HomeAchievementsStore.Message
import components.networkInterface.NetworkInterface
import kotlinx.coroutines.launch

class HomeAchievementsExecutor(
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
        }
    }

    private fun init() {
        scope.launch(CDispatcher) {
            nInterface.nStartLoading()
            try {
                val r = journalRepository.fetchAchievementsForStudent(
                    RFetchAchievementsForStudentReceive(studentLogin = state().login)
                )
                scope.launch {
                    dispatch(
                        Message.Inited(
                            achievements = r.list,
                            subjects = mapOf(-2 to "Дисциплина", -3 to "Общественная работа", -4 to "Творчество") + r.subjects //mvd-2 social-3 creative-3
                        )
                    )
                    nInterface.nSuccess()
                }
            } catch (e: Throwable) {
                nInterface.nError(
                    "Что-то пошло не так", e
                ) {
                    init()
                }
            }
        }
    }
}
