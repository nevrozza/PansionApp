package components.listDialog

import com.arkivanov.mvikotlin.core.store.Reducer

object ListDialogReducer : Reducer<ListDialogStore.State, ListDialogStore.Message> {
    override fun ListDialogStore.State.reduce(msg: ListDialogStore.Message): ListDialogStore.State {
        return when (msg) {
//            is ListDialogStore.Message.ErrorCalled -> copy(error = msg.error, isInProcess = false, onRetrySpecialClick = msg.onClick)
//            ListDialogStore.Message.ErrorCleared -> copy(error = "", onRetrySpecialClick = null)
            ListDialogStore.Message.HideDialog -> copy(isDialogShowing = false)
            is ListDialogStore.Message.ListInited -> copy(list = msg.list)

            is ListDialogStore.Message.ShowDialog -> {copy(isDialogShowing = true, x = if(msg.x != 65566556f) msg.x else x, y =  if(msg.x != 65566556f) msg.y else y)}
//            ListDialogStore.Message.StartProcess -> copy(isInProcess = true)
//            ListDialogStore.Message.StopProcess -> copy(isInProcess = false)
        }
    }
}