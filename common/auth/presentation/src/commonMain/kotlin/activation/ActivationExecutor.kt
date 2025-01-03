package activation

import AuthRepository
import SettingsRepository
import activation.ActivationStore.Intent
import activation.ActivationStore.Message
import activation.ActivationStore.State
import auth.CheckActivationReceive
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.launch

class ActivationExecutor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) :
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
            Intent.Init -> init()
            Intent.ChangeVerify -> dispatch(Message.VerifyChanged)
            is Intent.ChangeVerifyPassword -> dispatch(Message.VerifyPasswordChanged(intent.password))
        }
    }

    private fun init() {
        scope.launch {
            try {
                val r = authRepository.fetchLogins()
                dispatch(Message.Inited(logins = r.logins))
            } catch (_: Throwable) {

            }
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

    private fun changeStepOnActivation(state: State) {
        dispatch(Message.ProcessStarted)
        scope.launch {
            try {
                val response = authRepository.checkActivation(
                    CheckActivationReceive(
                        login = state.login
                    )
                )
                if (response.isActivated) {
                    dispatch(Message.AlreadyActivated)
                } else if (response.name == null) {
                    dispatch(Message.UserNotExisting)
                } else {
                    dispatch(Message.GoToActivationStep(response.name!!))
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
