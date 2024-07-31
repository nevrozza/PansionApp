import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import SettingsStore.Message
import components.listDialog.ListComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import view.Language
import view.ThemeTint
import view.isCanInDynamic

class SettingsExecutor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val colorModeListComponent: ListComponent
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.ClickOnQuit -> quit()
            is Intent.ChangeColorMode -> dispatch(Message.ColorModeChanged(intent.colorMode))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun quit() {

        GlobalScope.launch(CDispatcher) {

            try {
                authRepository.logout()
            } catch (_: Throwable) {

            }
        }
    }
}
