import com.arkivanov.mvikotlin.core.store.Store
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import auth.Device

interface SettingsStore : Store<Intent, State, Label> {
    data class State(
        val login: String,
        val isDevicesMenuOpened: Boolean = false,
        val deviceList: List<Device> = listOf(),
        val newColorMode: String? = null
    )

    sealed interface Intent {
        data object ClickOnQuit : Intent
        data class ChangeColorMode(val colorMode: String?) : Intent
        data object FetchDevices : Intent
        data class TerminateDevice(val id: String) : Intent
    }

    sealed interface Message {
        data class ColorModeChanged(val colorMode: String?) : Message
        data class DevicesFetched(val devices: List<Device>) : Message
    }

    sealed interface Label

}
