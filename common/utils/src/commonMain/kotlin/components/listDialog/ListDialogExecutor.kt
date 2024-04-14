package components.listDialog

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface

class ListDialogExecutor(
    private val nInterface: NetworkInterface,
    private val customOnDismiss: (() -> Unit)?
) :
    CoroutineExecutor<ListDialogStore.Intent, Unit, ListDialogStore.State, ListDialogStore.Message, ListDialogStore.Label>() {
    override fun executeIntent(intent: ListDialogStore.Intent) {
        when (intent) {
//            is ListDialogStore.Intent.CallError -> dispatch(ListDialogStore.Message.ErrorCalled(intent.error, intent.onClick))
//            is ListDialogStore.Intent.ClearError ->  {
//                dispatch(ListDialogStore.Message.ErrorCleared)
//                intent.onClick?.invoke()
//            }
            ListDialogStore.Intent.HideDialog -> {
                customOnDismiss?.invoke()
                dispatch(ListDialogStore.Message.HideDialog)
            }

            is ListDialogStore.Intent.InitList -> {
                dispatch(ListDialogStore.Message.ListInited(list = intent.list))
                nInterface.nSuccess()
            }

            is ListDialogStore.Intent.ShowDialog -> dispatch(
                ListDialogStore.Message.ShowDialog(
                    x = intent.x,
                    y = intent.y
                )
            )
//            ListDialogStore.Intent.StartProcess -> dispatch(ListDialogStore.Message.StartProcess)
//            ListDialogStore.Intent.StopProcess -> dispatch(ListDialogStore.Message.StopProcess)
        }
    }
}
