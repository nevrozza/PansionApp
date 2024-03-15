package components.cBottomSheet

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.cBottomSheet.CBottomSheetStore.Intent
import components.cBottomSheet.CBottomSheetStore.Label
import components.cBottomSheet.CBottomSheetStore.State
import components.cBottomSheet.CBottomSheetStore.Message

class CBottomSheetExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            Intent.HideSheet -> dispatch(Message.SheetShowingChanged(false))
            Intent.ShowSheet -> dispatch(Message.SheetShowingChanged(true))
        }
    }
}
