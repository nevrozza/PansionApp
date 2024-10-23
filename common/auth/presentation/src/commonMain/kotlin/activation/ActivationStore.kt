package activation

import activation.ActivationStore.Intent
import activation.ActivationStore.State
import com.arkivanov.mvikotlin.core.store.Store

interface ActivationStore : Store<Intent, State, Nothing> {
    data class State(
        val login: String = "",
        val name: String? = null,
        val password: String = "",
        val verifyPassword: String = "",
        val step: Step = Step.Login,
        val isInProcess: Boolean = false,
        val error: String = "",
        val isErrorShown: Boolean = false,
        val activated: Boolean = false,
        val isVerifyingPassword: Boolean = false,
        val logins: List<String> = emptyList()
    )

    sealed interface Intent {

        data object ChangeVerify : Intent

        data object ResetAll: Intent
        data class InputLogin(val login: String) : Intent
        data class InputPassword(val password: String) : Intent

        data class ChangeStep(val step: Step) : Intent
        data object ChangeStepOnActivation : Intent
        data object CheckToGoMain : Intent
        data object HideError : Intent

        data object Init : Intent

        data class ChangeVerifyPassword(val password: String) : Intent
    }

    sealed interface Message {
        data object VerifyChanged : Message

        data class VerifyPasswordChanged(val password: String) : Message


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
