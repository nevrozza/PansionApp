package admin

import com.arkivanov.mvikotlin.core.store.Reducer
import admin.AdminStore.State
import admin.AdminStore.Message

object AdminReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            else -> TODO()
        }
    }
}