package homeTasks

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import homeTasks.HomeTasksStore.Intent
import homeTasks.HomeTasksStore.Label
import homeTasks.HomeTasksStore.State
import homeTasks.HomeTasksStore.Message

class HomeTasksExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            else -> {}
        }
    }
}
