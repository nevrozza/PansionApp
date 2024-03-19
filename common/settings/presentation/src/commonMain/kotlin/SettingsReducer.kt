import com.arkivanov.mvikotlin.core.store.Reducer
import SettingsStore.State
import SettingsStore.Message

object SettingsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.ThemeTintChanged -> copy(themeTint = msg.tint.name)
            is Message.LanguageChanged -> copy(language = msg.language.name)
            is Message.ColorChanged -> copy(color = msg.color.name)
        }
    }
}