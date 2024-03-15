package components.networkInterface

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterfaceStore.Intent
import components.networkInterface.NetworkInterfaceStore.Label
import components.networkInterface.NetworkInterfaceStore.State
import components.networkInterface.NetworkInterfaceStore.Message
import kotlinx.coroutines.launch

class NetworkInterfaceExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.OnRetryClick -> {
                scope.launch {
                    intent.x()
                }
            }
        }
    }
}
