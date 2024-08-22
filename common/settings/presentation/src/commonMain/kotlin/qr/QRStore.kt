package qr

import com.arkivanov.mvikotlin.core.store.Store
import qr.QRStore.Intent
import qr.QRStore.Label
import qr.QRStore.State

interface QRStore : Store<Intent, State, Label> {
    data class State(
        val code: String = "",
        val isRegistration: Boolean,
        val deviceName: String = "",
        val deviceType: String = "",
    )

    sealed interface Intent {
        data class ChangeCode(val code: String) : Intent
        data object SendToServer: Intent
        data object SendToServerAtAll: Intent
        data object GoToNone: Intent
    }

    sealed interface Message {
        data class CodeChanged(val code: String) : Message
        data class AuthReceived(val deviceName: String, val deviceType: String) : Message
    }

    sealed interface Label

}
