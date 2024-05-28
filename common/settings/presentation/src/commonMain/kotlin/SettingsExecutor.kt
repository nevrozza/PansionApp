import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import SettingsStore.Message
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import view.Language
import view.ThemeTint
import view.isCanInDynamic

class SettingsExecutor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.ClickOnQuit -> quit()
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
