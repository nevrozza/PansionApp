package qr

import com.arkivanov.mvikotlin.core.store.Reducer
import qr.QRStore.State
import qr.QRStore.Message

object QRReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.CodeChanged -> copy(code = msg.code)
            is Message.AuthReceived -> copy(deviceType = msg.deviceType, deviceName = msg.deviceName)
        }
    }
}