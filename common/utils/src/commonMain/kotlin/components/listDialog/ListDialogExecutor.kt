package components.listDialog

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor

class ListDialogExecutor() :
    CoroutineExecutor<ListDialogStore.Intent, Unit, ListDialogStore.State, ListDialogStore.Message, ListDialogStore.Label>() {
    override fun executeIntent(intent: ListDialogStore.Intent, getState: () -> ListDialogStore.State) {
        when (intent) {
            is ListDialogStore.Intent.CallError -> dispatch(ListDialogStore.Message.ErrorCalled(intent.error, intent.onClick))
            is ListDialogStore.Intent.ClearError ->  {
                dispatch(ListDialogStore.Message.ErrorCleared)
                intent.onClick?.invoke()
            }
            ListDialogStore.Intent.HideDialog -> dispatch(ListDialogStore.Message.HideDialog)
            is ListDialogStore.Intent.InitList -> dispatch(ListDialogStore.Message.ListInited(list = intent.list))
            is ListDialogStore.Intent.ShowDialog -> dispatch(ListDialogStore.Message.ShowDialog(x = intent.x, y = intent.y))
            ListDialogStore.Intent.StartProcess -> dispatch(ListDialogStore.Message.StartProcess)
            ListDialogStore.Intent.StopProcess -> dispatch(ListDialogStore.Message.StopProcess)
        }
    }
}
