package homeTasksDialog

import com.arkivanov.mvikotlin.core.store.Reducer
import homeTasksDialog.HomeTasksDialogStore.State
import homeTasksDialog.HomeTasksDialogStore.Message

object HomeTasksDialogReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.HomeTasksUpdated -> copy(homeTasks = msg.homeTasks)
        }
    }
}