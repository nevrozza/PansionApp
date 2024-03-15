package components.cBottomSheet

import com.arkivanov.mvikotlin.core.store.Reducer
import components.cBottomSheet.CBottomSheetStore.State
import components.cBottomSheet.CBottomSheetStore.Message

object CBottomSheetReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.SheetShowingChanged -> copy(isDialogShowing = msg.isShowing)
        }
    }
}