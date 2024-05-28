package activation

import com.arkivanov.mvikotlin.core.store.Reducer
import activation.ActivationStore.State
import activation.ActivationStore.Message

object ActivationReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.LoginChanged -> copy(login = msg.login, isErrorShown = false)
            //is Message.StepChanged -> if(msg.step == ActivationStore.Step.Activation) copy(name = "Александра", step = msg.step) else copy(step = msg.step, name = null)

            Message.ErrorHided-> copy(isErrorShown = false)

            Message.ProcessStarted -> copy(isInProcess = true, isErrorShown = false)

            Message.AlreadyActivated -> copy(error = "Данный аккаунт уже активирован", isErrorShown = true, isInProcess = false)
            Message.UserNotExisting -> copy(error = "Данного аккаунта не существует", isErrorShown = true, isInProcess = false)
            is Message.CustomError -> copy(error = msg.error, isErrorShown = true, isInProcess = false)


            is Message.GoToActivationStep -> copy(name = msg.name, step = ActivationStore.Step.Activation, isErrorShown = false, isInProcess = false)
            Message.Activated -> copy(activated = true, isInProcess = false)
            is Message.StepChanged -> copy(step = msg.step, name = null)
            is Message.PasswordChanged -> copy(password = msg.password, isErrorShown = false)
            Message.AllReseted -> copy(
                login = "",
                name = null,
                password = "",
                step = ActivationStore.Step.Login,
                isInProcess = false,
                error = "",
                isErrorShown = false,
                activated = false
            )
        }
    }
}