package login

import AuthRepository
import auth.*
import activation.ActivationStore
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.launch
import login.LoginStore.Intent
import login.LoginStore.Label
import login.LoginStore.State
import login.LoginStore.Message

class LoginExecutor(private val authRepository: AuthRepository) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.InputLogin -> dispatch(Message.LoginChanged(intent.login))
            is Intent.InputPassword -> dispatch(Message.PasswordChanged(intent.password))
            Intent.CheckToGoMain -> checkToGoMain(getState())
            Intent.HideError -> dispatch(Message.ErrorHided)
        }


    }
    private fun checkToGoMain(state: State) {
        dispatch(Message.ProcessStarted)
        scope.launch {
            try {
                val r = authRepository.performLogin(state.login, state.password)
                if (r.activation.token.isNotBlank()) {
                    dispatch(Message.Logined)
                } else {
                    dispatch(Message.CustomError("Неправильный пароль или логин"))
                }
            }
            catch (e: ConnectTimeoutException) {
                dispatch(Message.CustomError("Не удаётся подключиться к серверу"))
            }
            catch (e: Throwable) {
                if (e.message.toString()
                        .commonPrefixWith("failed to connect to /") == "failed to connect to /"
                ) {
                    dispatch(Message.CustomError("Не удаётся подключиться к серверу"))
                } else if (
                    e.message.toString()
                        .commonPrefixWith("Failed to connect to /") == "Failed to connect to /"
                ) {
                    dispatch(Message.CustomError("Проверьте подключение к интернету"))
                } else {
                    dispatch(Message.CustomError("Что-то пошло не так =/"))
                }
            }
        }
    }
}
