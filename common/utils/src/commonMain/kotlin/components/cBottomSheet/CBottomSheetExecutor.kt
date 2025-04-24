package components.cBottomSheet

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cBottomSheet.CBottomSheetStore.Intent
import components.cBottomSheet.CBottomSheetStore.Label
import components.cBottomSheet.CBottomSheetStore.Message
import components.cBottomSheet.CBottomSheetStore.State

class CBottomSheetExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.HideSheet -> dispatch(Message.SheetShowingChanged(false))
            Intent.ShowSheet -> dispatch(Message.SheetShowingChanged(true))
        }
    }
}
