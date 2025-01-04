package homeTasksDialog

import CDispatcher
import JournalRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import homeTasksDialog.HomeTasksDialogStore.Intent
import homeTasksDialog.HomeTasksDialogStore.Label
import homeTasksDialog.HomeTasksDialogStore.State
import homeTasksDialog.HomeTasksDialogStore.Message
import homework.RFetchGroupHomeTasksReceive
import kotlinx.coroutines.launch

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
        scope.launch(CDispatcher) {
            try {
                nInterface.nStartLoading()
                val tasks = journalRepository.fetchGroupHomeTasks(RFetchGroupHomeTasksReceive(state().groupId)).tasks
                scope.launch {
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
