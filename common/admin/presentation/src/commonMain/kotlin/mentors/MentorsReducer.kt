package mentors

import com.arkivanov.mvikotlin.core.store.Reducer
import mentors.MentorsStore.State
import mentors.MentorsStore.Message

object MentorsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            else -> TODO()
        }
    }
}