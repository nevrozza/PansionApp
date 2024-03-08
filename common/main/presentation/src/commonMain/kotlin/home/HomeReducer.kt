package home

import com.arkivanov.mvikotlin.core.store.Reducer
import home.HomeStore.State
import home.HomeStore.Message

object HomeReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            else -> TODO()
        }
    }
}