package school

import com.arkivanov.mvikotlin.core.store.Reducer
import school.SchoolStore.State
import school.SchoolStore.Message

object SchoolReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.Inited -> copy(
                formId = msg.formId,
                formName = msg.formName,
                top = msg.top,
                formNum = msg.formNum
            )
        }
    }
}