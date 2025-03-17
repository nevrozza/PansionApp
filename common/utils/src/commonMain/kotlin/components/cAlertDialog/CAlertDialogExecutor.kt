package components.cAlertDialog

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CAlertDialogExecutor() :
    CoroutineExecutor<CAlertDialogStore.Intent, Unit, CAlertDialogStore.State, CAlertDialogStore.Message, CAlertDialogStore.Label>() {
    override fun executeIntent(
        intent: CAlertDialogStore.Intent
    ) {
        when (intent) {
            CAlertDialogStore.Intent.HideDialog -> scope.launch {
                // fix mercanie background
                if(state().needDelayWhenHide) {
                    delay(50)
                }
                dispatch(CAlertDialogStore.Message.HideDialog)
            }

            is CAlertDialogStore.Intent.ShowDialog -> dispatch(
                CAlertDialogStore.Message.ShowDialog
            )
        }
    }
}
