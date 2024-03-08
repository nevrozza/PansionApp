package activation

import com.arkivanov.mvikotlin.core.store.Store
import activation.ActivationStore.Intent
import activation.ActivationStore.State
import view.Language
import view.ThemeColors
import view.ThemeTint

interface ActivationStore : Store<Intent, State, Nothing> {
    data class State(
        val login: String = "",
        val name: String? = null,
        val password: String = "",
        val step: Step = Step.Choice,
        val isInProcess: Boolean = false,
        val error: String = "",
        val isErrorShown: Boolean = false,
        val themeTint: String,
        val color: String,
        val language: String,
        val activated: Boolean = false
    )

    sealed interface Intent {
        data class InputLogin(val login: String) : Intent
        data class InputPassword(val password: String) : Intent
        data object ChangeTint : Intent
        data object ChangeLanguage : Intent
        data object ChangeColor : Intent

        data class ChangeStep(val step: Step) : Intent
        data object ChangeStepOnActivation : Intent
        data object CheckToGoMain : Intent
        data object HideError : Intent
    }

    sealed interface Message {
        data class LoginChanged(val login: String) : Message
        data class PasswordChanged(val password: String) : Message
        data class ThemeTintChanged(val tint: ThemeTint) : Message
        data class LanguageChanged(val language: Language) : Message
        data class ColorChanged(val color: ThemeColors) : Message

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
