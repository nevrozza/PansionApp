package studentLines

import com.arkivanov.mvikotlin.core.store.Reducer
import studentLines.StudentLinesStore.State
import studentLines.StudentLinesStore.Message

object StudentLinesReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.StudentLinesInited -> copy(
                studentLines = msg.studentLines
            )
        }
    }
}