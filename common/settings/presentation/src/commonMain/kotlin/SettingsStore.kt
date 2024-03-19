import com.arkivanov.mvikotlin.core.store.Store
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import view.Language
import view.ThemeColors
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
        val themeTint: String,
        val color: String,
        val language: String,
        val isDevicesMenuOpened: Boolean = false,
        val deviceList: List<Device> = listOf()
    )

    sealed interface Intent {
//        data object ClickOnQuit : Intent
        data object ClickOnQuit : Intent

        data object ChangeTint : Intent
        data object ChangeLanguage : Intent
        data object ChangeColor : Intent
    }

    sealed interface Message {
        data class ThemeTintChanged(val tint: ThemeTint) : Message
        data class LanguageChanged(val language: Language) : Message
        data class ColorChanged(val color: ThemeColors) : Message
    }

    sealed interface Label

}
