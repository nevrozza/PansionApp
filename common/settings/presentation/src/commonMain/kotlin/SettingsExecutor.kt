import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import SettingsStore.Message
import auth.RTerminateDeviceReceive
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
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
    private val colorModeListComponent: ListComponent,
    private val nDevicesInterface: NetworkInterface
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.ClickOnQuit -> quit()
            is Intent.ChangeColorMode -> dispatch(Message.ColorModeChanged(intent.colorMode))
            is Intent.FetchDevices -> fetchDevices()
            is Intent.TerminateDevice -> terminate(intent.id)
        }
    }

    private fun terminate(id: String) {
        scope.launch(CDispatcher) {
            println("TEXTIK")
            try {
                settingsRepository.terminateDevice(RTerminateDeviceReceive(id = id))
                fetchDevices()
            } catch (_: Throwable) {}
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

    private fun fetchDevices() {
        scope.launch(CDispatcher) {
            try {
                val devices = settingsRepository.fetchDevices()
                scope.launch {
                    dispatch(Message.DevicesFetched(devices.tokens.reversed()))
                }
            } catch (_: Throwable) {
                nDevicesInterface.nError("Ошибка") {
                    fetchDevices()
                }
            }
        }
    }
}
