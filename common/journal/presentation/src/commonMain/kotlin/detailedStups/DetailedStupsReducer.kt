package detailedStups

import com.arkivanov.mvikotlin.core.store.Reducer
import detailedStups.DetailedStupsStore.State
import detailedStups.DetailedStupsStore.Message

object DetailedStupsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.SubjectsUpdated -> copy(subjects = msg.subjects)
        }
    }
}