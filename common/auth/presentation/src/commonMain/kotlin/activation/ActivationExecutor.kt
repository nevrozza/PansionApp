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
import view.ThemeColors
import view.ThemeTint
import view.isCanInDynamic

class ActivationExecutor(private val settingsRepository: SettingsRepository, private val authRepository: AuthRepository) :
    CoroutineExecutor<Intent, Unit, State, Message, Nothing>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            is Intent.InputLogin -> dispatch(Message.LoginChanged(intent.login))
            is Intent.InputPassword -> dispatch(Message.PasswordChanged(intent.password))
            is Intent.ChangeStepOnActivation -> changeStepOnActivation(getState())
            is Intent.CheckToGoMain -> checkToGoMain(getState())
            Intent.ChangeTint -> changeTint(getState())
            Intent.ChangeLanguage -> changeLanguage(getState())
            Intent.ChangeColor -> changeColor(getState())
            is Intent.ChangeStep -> dispatch(Message.StepChanged(intent.step))
            Intent.HideError -> dispatch(Message.ErrorHided)
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

    private fun changeTint(state: State) {
        when (state.themeTint) {
            ThemeTint.Auto.name -> {
                settingsRepository.saveTint(ThemeTint.Dark.name)
                dispatch(Message.ThemeTintChanged(ThemeTint.Dark))
            }

            ThemeTint.Dark.name -> {
                settingsRepository.saveTint(ThemeTint.Light.name)
                dispatch(Message.ThemeTintChanged(ThemeTint.Light))
            }

            else -> {
                settingsRepository.saveTint(ThemeTint.Auto.name)
                dispatch(Message.ThemeTintChanged(ThemeTint.Auto))
            }
        }
    }

    private fun changeColor(state: State) {
        when (state.color) {
            ThemeColors.Default.name -> {
                settingsRepository.saveColor(ThemeColors.Green.name)
                dispatch(Message.ColorChanged(ThemeColors.Green))
            }

            ThemeColors.Green.name -> {
                settingsRepository.saveColor(ThemeColors.Red.name)
                dispatch(Message.ColorChanged(ThemeColors.Red))
            }

            ThemeColors.Red.name -> {
                settingsRepository.saveColor(ThemeColors.Yellow.name)
                dispatch(Message.ColorChanged(ThemeColors.Yellow))
            }

            ThemeColors.Yellow.name -> {
                val color = if (isCanInDynamic()) ThemeColors.Dynamic else ThemeColors.Default

                settingsRepository.saveColor(color.name)
                dispatch(Message.ColorChanged(color))

            }

            else -> {

                settingsRepository.saveColor(ThemeColors.Default.name)
                dispatch(Message.ColorChanged(ThemeColors.Default))
            }
        }
    }

    private fun changeLanguage(state: State) {
        when (state.language) {
            Language.Russian.name -> {
//                settingsRepository.saveTint(ThemeTint.Dark.name)
//                dispatch(Message.ThemeTintChanged(ThemeTint.Dark))
            }

            else -> {
                settingsRepository.saveLanguage(Language.Russian.name)
                dispatch(Message.LanguageChanged(Language.Russian))
            }
        }
    }

}
