package components.networkInterface

import com.arkivanov.mvikotlin.core.store.Reducer
import components.networkInterface.NetworkInterfaceStore.State
import components.networkInterface.NetworkInterfaceStore.Message

object NetworkInterfaceReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            else -> TODO()
        }
    }
}