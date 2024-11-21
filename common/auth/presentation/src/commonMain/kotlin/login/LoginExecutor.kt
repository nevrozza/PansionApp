package login

import AuthRepository
import CommonPlatformConfiguration
import auth.*
import activation.ActivationStore
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import di.Inject
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.launch
import login.LoginStore.Intent
import login.LoginStore.Label
import login.LoginStore.State
import login.LoginStore.Message

class LoginExecutor(private val authRepository: AuthRepository) :
    CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.InputLogin -> dispatch(Message.LoginChanged(intent.login))
            is Intent.InputPassword -> dispatch(Message.PasswordChanged(intent.password))
            Intent.CheckToGoMain -> checkToGoMain(state())
            Intent.HideError -> dispatch(Message.ErrorHided)
            Intent.GetQrToken -> getQRToken()
        }


    }

    private fun startQRPolling() {
        scope.launch {
            try {
                val platformConfiguration: CommonPlatformConfiguration = Inject.instance()
                val r = authRepository.pollQrToken(
                    RFetchQrTokenReceive(
                        platformConfiguration.deviceId,
                        "",
                        ""
                    )
                )
                if (r.activation.token.isNotBlank()) {
                    authRepository.saveUser(
                        avatarId = r.avatarId,
                        a = r.activation
                    )
                    dispatch(Message.Logined)
                }


            } catch (e: Throwable) {
                getQRToken()
            }
        }
    }


    private fun getQRToken() {
        scope.launch {
            try {
                val platformConfiguration: CommonPlatformConfiguration = Inject.instance()
                val r = authRepository.fetchQrToken(
                    RFetchQrTokenReceive(
                        platformConfiguration.deviceId,
                        platformConfiguration.deviceType,
                        platformConfiguration.deviceName
                    )
                )
                dispatch(Message.QrTokenGet(r.token))
                startQRPolling()
            } catch (_: Throwable) {

            }
        }
    }

    private fun checkToGoMain(state: State) {
        dispatch(Message.ProcessStarted)
        scope.launch {
            try {
                val r = authRepository.performLogin(state.login, state.password)
                val token = r.activation.token
                if (token == "password") {
                    dispatch(Message.CustomError("Неправильный пароль"))
                } else if (token == "user") {
                    dispatch(Message.CustomError("Неправильный логин"))
                } else if (token == "deactivated") {
                    dispatch(Message.CustomError("Аккаунт деактивирован"))
                } else if (r.activation.token.isNotBlank()) {
                    dispatch(Message.Logined)
                } else {
                    dispatch(Message.CustomError("Неправильный пароль или логин"))
                }
            } catch (e: ConnectTimeoutException) {
                dispatch(Message.CustomError("Не удаётся подключиться к серверу"))
            } catch (e: Throwable) {
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
