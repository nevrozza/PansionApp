package profile

import com.arkivanov.mvikotlin.core.store.Reducer
import profile.ProfileStore.State
import profile.ProfileStore.Message

object ProfileReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            else -> TODO()
        }
    }
}