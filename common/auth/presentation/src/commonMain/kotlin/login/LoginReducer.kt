package login

import com.arkivanov.mvikotlin.core.store.Reducer
import login.LoginStore.State
import login.LoginStore.Message

object LoginReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.LoginChanged -> copy(login = msg.login, isErrorShown = false)
            is Message.PasswordChanged -> copy(password = msg.password, isErrorShown = false)
            Message.ProcessStarted -> copy(isInProcess = true, isErrorShown = false)
            is Message.CustomError -> copy(error = msg.error, isErrorShown = true, isInProcess = false)
            Message.Logined -> copy(logined = true, isInProcess = false)
            Message.ErrorHided -> copy(isErrorShown = false)
            is Message.QrTokenGet -> copy(qrToken = msg.qrToken)
        }
    }
}