package components.cAlertDialog
import com.arkivanov.mvikotlin.core.store.Store
interface CAlertDialogStore : Store<CAlertDialogStore.Intent, CAlertDialogStore.State, CAlertDialogStore.Label> {

    data class State(
        val isDialogShowing: Boolean = false,
        val isInProcess: Boolean = false,
        val error: String = "",
        val onRetrySpecialClick: (() -> Unit)? = null,
        val onAcceptClick: (() -> Unit)? = null,
        val onDeclineClick: (() -> Unit)? = null,
        val isButtonEnabled: Boolean = true
    )

    sealed interface Intent {
        data object HideDialog : Intent
        data object ShowDialog : Intent
        data object StartProcess : Intent
        data object StopProcess : Intent
        data class CallError(val error: String, val onClick: (() -> Unit)? = null) : Intent
        data class ClearError(val onClick: (() -> Unit)? = null) : Intent

    }

    sealed interface Message {
        data object HideDialog : Message
        data object ShowDialog : Message
        data object StartProcess : Message
        data object StopProcess : Message
        data class ErrorCalled(val error: String, val onClick: (() -> Unit)?) : Message
        data object ErrorCleared : Message
    }

    sealed interface Label

}
