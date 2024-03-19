package home

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import home.HomeStore.Intent
import home.HomeStore.Label
import home.HomeStore.State
import home.HomeStore.Message

class HomeExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            else -> TODO()
        }
    }
}
