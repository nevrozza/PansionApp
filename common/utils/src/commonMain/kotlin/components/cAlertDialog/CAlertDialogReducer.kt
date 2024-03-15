package components.cAlertDialog

import com.arkivanov.mvikotlin.core.store.Reducer

object CAlertDialogReducer : Reducer<CAlertDialogStore.State, CAlertDialogStore.Message> {
    override fun CAlertDialogStore.State.reduce(msg: CAlertDialogStore.Message): CAlertDialogStore.State {
        return when (msg) {
//            is CAlertDialogStore.Message.ErrorCalled -> copy(error = msg.error, isInProcess = false, onRetrySpecialClick = msg.onClick)
//            CAlertDialogStore.Message.ErrorCleared -> copy(error = "", onRetrySpecialClick = null)
            CAlertDialogStore.Message.HideDialog -> copy(isDialogShowing = false)
            is CAlertDialogStore.Message.ShowDialog -> {copy(isDialogShowing = true)}
//            CAlertDialogStore.Message.StartProcess -> copy(isInProcess = true)
//            CAlertDialogStore.Message.StopProcess -> copy(isInProcess = false)
        }
    }
}