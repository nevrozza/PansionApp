package activation

import com.arkivanov.mvikotlin.core.store.Store
import activation.ActivationStore.Intent
import activation.ActivationStore.State
import view.Language
import view.ThemeTint

interface ActivationStore : Store<Intent, State, Nothing> {
    data class State(
        val login: String = "",
        val name: String? = null,
        val password: String = "",
        val step: Step = Step.Login,
        val isInProcess: Boolean = false,
        val error: String = "",
        val isErrorShown: Boolean = false,
        val activated: Boolean = false,
        val logins: List<String> = emptyList()
    )

    sealed interface Intent {
        data object ResetAll: Intent
        data class InputLogin(val login: String) : Intent
        data class InputPassword(val password: String) : Intent

        data class ChangeStep(val step: Step) : Intent
        data object ChangeStepOnActivation : Intent
        data object CheckToGoMain : Intent
        data object HideError : Intent

        data object Init : Intent
    }

    sealed interface Message {
        data class Inited(val logins: List<String>) : Message
        data object AllReseted: Message
        data class LoginChanged(val login: String) : Message
        data class PasswordChanged(val password: String) : Message

        data object ErrorHided : Message

        data object ProcessStarted : Message
        data object AlreadyActivated : Message
        data object UserNotExisting : Message
        data class GoToActivationStep(val name: String) : Message
        data class StepChanged(val step: Step) : Message
        data class CustomError(val error: String) : Message
        data object Activated : Message
    }

    sealed interface Step {
        data object Login : Step
        data object Choice : Step
        data object Activation : Step
    }
}
