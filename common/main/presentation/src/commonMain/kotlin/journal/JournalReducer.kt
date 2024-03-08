package journal

import com.arkivanov.mvikotlin.core.store.Reducer
import journal.JournalStore.State
import journal.JournalStore.Message

object JournalReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.StudentsInGroupUpdated -> copy(studentsInGroup = msg.students, currentGroupId = msg.groupId)
        }
    }
}