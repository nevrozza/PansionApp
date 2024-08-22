package login

import activation.ActivationStore
import com.arkivanov.mvikotlin.core.store.Store
import login.LoginStore.Intent
import login.LoginStore.Label
import login.LoginStore.State

interface LoginStore : Store<Intent, State, Label> {
    data class State (
        val title: String = "Вход",
        val login: String = "",
        val password: String = "",
        val isInProcess: Boolean = false,
        val error: String = "",
        val isErrorShown: Boolean = false,
        val logined: Boolean = false,

        val qrToken: String = ""
    )

    sealed interface Intent {
        data class InputLogin(val login: String) : Intent
        data class InputPassword(val password: String) : Intent
        data object CheckToGoMain : Intent
        data object HideError : Intent

        data object GetQrToken : Intent
    }

    sealed interface Message {
        data class LoginChanged(val login: String) : Message
        data class PasswordChanged(val password: String) : Message
        data object ProcessStarted : Message
        data class CustomError(val error: String) : Message
        data object Logined : Message
        data object ErrorHided : Message

        data class QrTokenGet(val qrToken: String) : Message
    }

    sealed interface Label

}
