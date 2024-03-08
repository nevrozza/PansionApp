package students

import com.arkivanov.mvikotlin.core.store.Reducer
import students.StudentsStore.State
import students.StudentsStore.Message

object StudentsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            else -> TODO()
        }
    }
}