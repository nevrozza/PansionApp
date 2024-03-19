package components.cAlertDialog
import com.arkivanov.mvikotlin.core.store.Store
interface CAlertDialogStore : Store<CAlertDialogStore.Intent, CAlertDialogStore.State, CAlertDialogStore.Label> {

    data class State(
        val isDialogShowing: Boolean = false,
//        val onRetrySpecialClick: (() -> Unit)? = null,
        val onAcceptClick: (() -> Unit),
        val onDeclineClick: (() -> Unit),
        val isButtonEnabled: Boolean = true,
        val needDelayWhenHide: Boolean
    )

    sealed interface Intent {
        data object HideDialog : Intent
        data object ShowDialog : Intent
//        data class CallError(val error: String, val onClick: (() -> Unit)? = null) : Intent
//        data class ClearError(val onClick: (() -> Unit)? = null) : Intent

    }

    sealed interface Message {
        data object HideDialog : Message
        data object ShowDialog : Message
//        data class ErrorCalled(val error: String, val onClick: (() -> Unit)?) : Message
//        data object ErrorCleared : Message
    }

    sealed interface Label

}
