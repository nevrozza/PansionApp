import com.arkivanov.mvikotlin.core.store.Reducer
import SettingsStore.State
import SettingsStore.Message

object SettingsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            else -> {copy()}
        }
    }
}