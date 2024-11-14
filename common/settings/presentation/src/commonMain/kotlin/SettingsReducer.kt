import com.arkivanov.mvikotlin.core.store.Reducer
import SettingsStore.State
import SettingsStore.Message

object SettingsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.ColorModeChanged -> copy(newColorMode = msg.colorMode)
            is Message.DevicesFetched -> copy(deviceList = msg.devices)
            is Message.SecondLoginChanged -> copy(secondLogin = msg.secondLogin, eSecondLogin = msg.secondLogin ?: "")
            is Message.ESecondLogin -> copy(eSecondLogin = msg.secondLogin)
            is Message.IsMarkTableDefaultChanged -> copy(isMarkTableDefault = msg.isDefault)
            is Message.IsPlusDsStupsEnabledChanged -> copy(isPlusDsStupsEnabled = msg.isEnabled)
        }
    }
}