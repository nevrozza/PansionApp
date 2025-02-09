package homeTasksDialog

import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import deviceSupport.launchIO
import deviceSupport.withMain
import homeTasksDialog.HomeTasksDialogStore.Intent
import homeTasksDialog.HomeTasksDialogStore.Label
import homeTasksDialog.HomeTasksDialogStore.Message
import homeTasksDialog.HomeTasksDialogStore.State
import homework.RFetchGroupHomeTasksReceive

class HomeTasksDialogExecutor(
    private val journalRepository: JournalRepository,
    private val nInterface: NetworkInterface
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
                val tasks = journalRepository.fetchGroupHomeTasks(RFetchGroupHomeTasksReceive(state().groupId)).tasks
                withMain {
                    dispatch(Message.HomeTasksUpdated(tasks.reversed()))
                }
                nInterface.nSuccess()
            } catch (e: Throwable) {
                nInterface.nError("Не удалось загрузить ДЗ", e) {
                    init()
                }
            }
        }
    }
}
