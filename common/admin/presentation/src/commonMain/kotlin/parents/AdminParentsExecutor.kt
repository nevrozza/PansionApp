package parents

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import parents.AdminParentsStore.Intent
import parents.AdminParentsStore.Label
import parents.AdminParentsStore.State
import parents.AdminParentsStore.Message

class AdminParentsExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            else -> {}
        }
    }
}
