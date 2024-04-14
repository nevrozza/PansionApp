package dnevnikRuMarks

import com.arkivanov.mvikotlin.core.store.Reducer
import dnevnikRuMarks.DnevnikRuMarkStore.State
import dnevnikRuMarks.DnevnikRuMarkStore.Message

object DnevnikRuMarkReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.SubjectsUpdated -> copy(subjects = msg.subjects)
            is Message.IsQuartersInited -> copy(isQuarters = msg.isQuarters, tabIndex = msg.tabIndex)
            is Message.OnTabClicked -> copy(tabIndex = msg.index)
        }
    }
}