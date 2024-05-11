package components.mpChose

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface

class mpChoseExecutor(
    private val nInterface: NetworkInterface,
    private val customOnDismiss: (() -> Unit)?
) :
    CoroutineExecutor<mpChoseStore.Intent, Unit, mpChoseStore.State, mpChoseStore.Message, mpChoseStore.Label>() {
    override fun executeIntent(intent: mpChoseStore.Intent) {
        when (intent) {
            mpChoseStore.Intent.HideDialog -> {
                customOnDismiss?.invoke()
                dispatch(mpChoseStore.Message.HideDialog)
            }


            is mpChoseStore.Intent.ShowDialog -> dispatch(
                mpChoseStore.Message.ShowDialog
            )
        }
    }
}
