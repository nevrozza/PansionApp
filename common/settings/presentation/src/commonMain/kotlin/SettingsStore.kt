import com.arkivanov.mvikotlin.core.store.Store
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import auth.Device

interface SettingsStore : Store<Intent, State, Label> {
    data class State(
        val login: String,
        val secondLogin: String? = null,
        val eSecondLogin: String = "",
        val isDevicesMenuOpened: Boolean = false,
        val deviceList: List<Device> = listOf(),
        val newColorMode: String? = null,

        val isMarkTableDefault: Boolean,
        val isPlusDsStupsEnabled: Boolean
    )

    sealed interface Intent {
        data object ClickOnQuit : Intent
        data class ChangeColorMode(val colorMode: String?) : Intent
        data object FetchDevices : Intent
        data class TerminateDevice(val id: String) : Intent
        data class ESecondLogin(val secondLogin: String) : Intent
        data object SaveSecondLogin : Intent

        data object ChangeIsMarkTableDefault : Intent
        data object ChangeIsPlusDsStupsEnabled : Intent

    }

    sealed interface Message {
        data class ESecondLogin(val secondLogin: String) : Message
        data class ColorModeChanged(val colorMode: String?) : Message
        data class DevicesFetched(val devices: List<Device>) : Message
        data class SecondLoginChanged(val secondLogin: String?) : Message

        data class IsMarkTableDefaultChanged(val isDefault: Boolean) : Message
        data class IsPlusDsStupsEnabledChanged(val isEnabled: Boolean) : Message
    }

    sealed interface Label

}
