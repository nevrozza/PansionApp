import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import SettingsStore.Message
import kotlinx.coroutines.launch
import view.Language
import view.ThemeColors
import view.ThemeTint
import view.isCanInDynamic

class SettingsExecutor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.ChangeTint -> changeTint(state())
            Intent.ChangeLanguage -> changeLanguage(state())
            Intent.ChangeColor -> changeColor(state())
            Intent.ClickOnQuit -> quit()
        }
    }

    private fun quit() {

        scope.launch {

            try {
                authRepository.logout()
            } catch (_: Throwable) {

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
