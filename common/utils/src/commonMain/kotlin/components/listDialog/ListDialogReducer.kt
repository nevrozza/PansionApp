package components.listDialog

import com.arkivanov.mvikotlin.core.store.Reducer

object ListDialogReducer : Reducer<ListDialogStore.State, ListDialogStore.Message> {
    override fun ListDialogStore.State.reduce(msg: ListDialogStore.Message): ListDialogStore.State {
        return when (msg) {
            ListDialogStore.Message.HideDialog -> copy(isDialogShowing = false)
            is ListDialogStore.Message.ListInited -> copy(list = msg.list)

            is ListDialogStore.Message.ShowDialog -> {copy(isDialogShowing = true)}
        }
    }
}