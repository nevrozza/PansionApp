package admin

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import admin.AdminStore.Intent
import admin.AdminStore.Label
import admin.AdminStore.State
import admin.AdminStore.Message

class AdminExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            else -> {}
        }
    }
}
