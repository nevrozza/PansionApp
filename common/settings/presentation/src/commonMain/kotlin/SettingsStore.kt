import com.arkivanov.mvikotlin.core.store.Store
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import view.Language
import view.ThemeTint

data class Device(
    val deviceId: String,
    val deviceName: String,
    val deviceType: String,
    val time: String
)

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
    }

    sealed interface Message {
        data class ColorModeChanged(val colorMode: String?) : Message
    }

    sealed interface Label

}
