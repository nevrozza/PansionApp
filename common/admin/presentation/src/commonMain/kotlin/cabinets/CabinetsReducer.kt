package cabinets

import com.arkivanov.mvikotlin.core.store.Reducer
import cabinets.CabinetsStore.State
import cabinets.CabinetsStore.Message

object CabinetsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.ListUpdated -> copy(cabinets = msg.cabinets)
            is Message.TeachersInited -> copy(teachers = msg.teachers)
        }
    }
}