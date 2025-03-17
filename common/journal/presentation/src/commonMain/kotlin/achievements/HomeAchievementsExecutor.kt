package achievements

import JournalRepository
import achievements.HomeAchievementsStore.Intent
import achievements.HomeAchievementsStore.Label
import achievements.HomeAchievementsStore.Message
import achievements.HomeAchievementsStore.State
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject

class HomeAchievementsExecutor(
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository = Inject.instance(),
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeAction(action: Unit) {
        init()
    }


    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Init -> init()
        }
    }

    private fun init() {
        scope.launchIO {
            nInterface.nStartLoading()
            try {
                val r = journalRepository.fetchAchievementsForStudent(
                    RFetchAchievementsForStudentReceive(studentLogin = state().login)
                )
                withMain {
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
