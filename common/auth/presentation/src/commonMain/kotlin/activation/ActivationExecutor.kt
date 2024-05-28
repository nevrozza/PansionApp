package activation

import AuthRepository
import SettingsRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import activation.ActivationStore.Intent
import activation.ActivationStore.State
import activation.ActivationStore.Message
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.launch
import login.LoginStore
import view.Language
import view.ThemeTint
import view.isCanInDynamic

class ActivationExecutor(private val settingsRepository: SettingsRepository, private val authRepository: AuthRepository) :
    CoroutineExecutor<Intent, Unit, State, Message, Nothing>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.InputLogin -> dispatch(Message.LoginChanged(intent.login))
            is Intent.InputPassword -> dispatch(Message.PasswordChanged(intent.password))
            is Intent.ChangeStepOnActivation -> changeStepOnActivation(state())
            is Intent.CheckToGoMain -> checkToGoMain(state())
            is Intent.ChangeStep -> dispatch(Message.StepChanged(intent.step))
            Intent.HideError -> dispatch(Message.ErrorHided)
            Intent.ResetAll -> dispatch(Message.AllReseted)
        }
    }

    private fun checkToGoMain(state: State) {
        dispatch(Message.ProcessStarted)
        scope.launch {
            try {
                val response = authRepository.activate(state.login, state.password)
                if (response.token.isNotBlank()) {
                    dispatch(Message.Activated)
                } else {
                    dispatch(Message.CustomError("Произошло что-то очень странное..."))
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

    private fun changeStepOnActivation(state: State) {
        dispatch(Message.ProcessStarted)
        scope.launch {
            try {
                val response = authRepository.checkActivation(state.login)
                if (response.isActivated) {
                    dispatch(Message.AlreadyActivated)
                } else if (response.name == null) {
                    dispatch(Message.UserNotExisting)
                } else {
                    dispatch(Message.GoToActivationStep(response.name!!))
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
