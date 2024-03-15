package components.listDialog

import com.arkivanov.mvikotlin.core.store.Store
interface ListDialogStore : Store<ListDialogStore.Intent, ListDialogStore.State, ListDialogStore.Label> {

    data class State(
        val list: List<ListItem> = emptyList(),
        val isDialogShowing: Boolean = false,
//        val isInProcess: Boolean = false,
//        val error: String = "",
        val x: Float = 0.0f,
        val y: Float = 0.0f,
//        val onRetrySpecialClick: (() -> Unit)? = null
    )

    sealed interface Intent {
        data class InitList(val list: List<ListItem>) : Intent
        data object HideDialog : Intent
        data class ShowDialog(val x: Float, val y: Float) : Intent
//        data object StartProcess : Intent
//        data object StopProcess : Intent
//        data class CallError(val error: String, val onClick: (() -> Unit)? = null) : Intent
//        data class ClearError(val onClick: (() -> Unit)? = null) : Intent

    }

    sealed interface Message {
        data class ListInited(val list: List<ListItem>) : Message
        data object HideDialog : Message
        data class ShowDialog(val x: Float, val y: Float) : Message
//        data object StartProcess : Message
//        data object StopProcess : Message
//        data class ErrorCalled(val error: String, val onClick: (() -> Unit)?) : Message
//        data object ErrorCleared : Message
    }

    sealed interface Label

}
