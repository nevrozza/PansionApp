package parents

import com.arkivanov.mvikotlin.core.store.Reducer
import parents.AdminParentsStore.State
import parents.AdminParentsStore.Message

object AdminParentsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            else -> TODO()
        }
    }
}