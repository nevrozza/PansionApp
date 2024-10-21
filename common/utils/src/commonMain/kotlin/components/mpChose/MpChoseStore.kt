package components.mpChose

import com.arkivanov.mvikotlin.core.store.Store
interface MpChoseStore : Store<MpChoseStore.Intent, MpChoseStore.State, MpChoseStore.Label> {

    data class State(
        val isDialogShowing: Boolean = false
    )

    sealed interface Intent {
        data object HideDialog : Intent
        data object ShowDialog : Intent
    }

    sealed interface Message {
        data object HideDialog : Message
        data object ShowDialog : Message
    }

    sealed interface Label

}
