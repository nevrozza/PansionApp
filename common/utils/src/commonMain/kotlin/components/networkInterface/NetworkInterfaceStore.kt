package components.networkInterface

import com.arkivanov.mvikotlin.core.store.Store
import components.networkInterface.NetworkInterfaceStore.Intent
import components.networkInterface.NetworkInterfaceStore.Label
import components.networkInterface.NetworkInterfaceStore.State

interface NetworkInterfaceStore : Store<Intent, State, Label> {
    object State

    sealed interface Intent {
        data class OnRetryClick(val x: () -> Unit) : Intent
    }

    sealed interface Message

    sealed interface Label

}
