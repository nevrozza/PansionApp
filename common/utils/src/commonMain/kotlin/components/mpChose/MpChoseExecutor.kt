package components.mpChose

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface

class MpChoseExecutor(
    private val nInterface: NetworkInterface,
    private val customOnDismiss: (() -> Unit)?
) :
    CoroutineExecutor<MpChoseStore.Intent, Unit, MpChoseStore.State, MpChoseStore.Message, MpChoseStore.Label>() {
    override fun executeIntent(intent: MpChoseStore.Intent) {
        when (intent) {
            MpChoseStore.Intent.HideDialog -> {
                customOnDismiss?.invoke()
                dispatch(MpChoseStore.Message.HideDialog)
            }


            is MpChoseStore.Intent.ShowDialog -> dispatch(
                MpChoseStore.Message.ShowDialog
            )
        }
    }
}
