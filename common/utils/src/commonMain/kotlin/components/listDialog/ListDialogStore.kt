package components.listDialog

import com.arkivanov.mvikotlin.core.store.Store
interface ListDialogStore : Store<ListDialogStore.Intent, ListDialogStore.State, ListDialogStore.Label> {

    data class State(
        val list: List<ListItem> = emptyList(),
        val isDialogShowing: Boolean = false,
        val x: Float = 0.0f,
        val y: Float = 0.0f
    )

    sealed interface Intent {
        data class InitList(val list: List<ListItem>) : Intent
        data object HideDialog : Intent
        data object ShowDialog : Intent
    }

    sealed interface Message {
        data class ListInited(val list: List<ListItem>) : Message
        data object HideDialog : Message
        data object ShowDialog : Message
    }

    sealed interface Label

}
