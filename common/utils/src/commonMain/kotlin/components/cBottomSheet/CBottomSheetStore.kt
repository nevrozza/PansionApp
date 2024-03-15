package components.cBottomSheet

import com.arkivanov.mvikotlin.core.store.Store
import components.cBottomSheet.CBottomSheetStore.Intent
import components.cBottomSheet.CBottomSheetStore.Label
import components.cBottomSheet.CBottomSheetStore.State

interface CBottomSheetStore : Store<Intent, State, Label> {
    data class State(
        val isDialogShowing: Boolean = false
    )

    sealed interface Intent {
        data object HideSheet : Intent
        data object ShowSheet : Intent
    }

    sealed interface Message {
        data class SheetShowingChanged(val isShowing: Boolean) : Message
    }

    sealed interface Label

}
