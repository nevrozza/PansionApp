package activation

import AuthRepository
import activation.ActivationStore.Intent
import activation.ActivationStore.Message
import activation.ActivationStore.State
import auth.CheckActivationReceive
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import deviceSupport.launchIO
import deviceSupport.withMain
import di.Inject
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.CoroutineScope

class ActivationExecutor(
    private val authRepository: AuthRepository = Inject.instance()
) :
    CoroutineExecutor<Intent, Unit, State, Message, ActivationStore.Label>() {
    override fun executeAction(action: Unit) {
        init()
    }


    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.InputLogin -> dispatch(Message.LoginChanged(intent.login))
            is Intent.InputPassword -> dispatch(Message.PasswordChanged(intent.password))
            is Intent.ChangeStepOnActivation -> changeStepOnActivation()
            is Intent.CheckToGoMain -> checkToGoMain()
            is Intent.ChangeStep -> dispatch(Message.StepChanged(intent.step))
            Intent.HideError -> dispatch(Message.ErrorHided)
            Intent.ResetAll -> dispatch(Message.AllReseted)
            Intent.Init -> init()
            Intent.ChangeVerify -> dispatch(Message.VerifyChanged)
            is Intent.ChangeVerifyPassword -> dispatch(Message.VerifyPasswordChanged(intent.password))
        }
    }

    private fun init() {
        scope.launchIO {
            try {
                val r = authRepository.fetchLogins()
                withMain {
                    dispatch(Message.Inited(logins = r.logins))
                }
            } catch (_: Throwable) {

            }
        }
    }

    private fun checkToGoMain() {
        dispatch(Message.ProcessStarted)
        defaultActivationBody {
            val response = authRepository.activate(state().login, state().password)
            withMain {
                if (response.token.isNotBlank()) {
//                    dispatch(Message.Activated)
                    publish(ActivationStore.Label.Activated)
                } else {
                    dispatch(Message.CustomError("Произошло что-то очень странное..."))
                }
            }
        }
    }

    private fun changeStepOnActivation() {
        dispatch(Message.ProcessStarted)
        defaultActivationBody {
            val response = authRepository.checkActivation(
                CheckActivationReceive(
                    login = state().login
                )
            )
            withMain {
                if (response.isActivated) {
                    dispatch(Message.AlreadyActivated)
                } else if (response.name == null) {
                    dispatch(Message.UserNotExisting)
                } else {
                    dispatch(Message.GoToActivationStep(response.name!!))
                }
            }
        }
    }


    private fun defaultActivationBody(tryBlock: suspend CoroutineScope.() -> Unit) {
        scope.launchIO {
            try {
                tryBlock()
            } catch (e: ConnectTimeoutException) {
                println(e)
                withMain {
                    dispatch(Message.CustomError("Не удаётся подключиться к серверу"))
                }
            } catch (e: Throwable) {
                println(e)
                withMain {
                    if (e.message.toString()
                            .commonPrefixWith("failed to connect to /") == "failed to connect to /"
                    ) {
                        dispatch(Message.CustomError("Не удаётся подключиться к серверу"))
                    } else if (
                        e.message.toString()
                            .commonPrefixWith("Failed to connect to /") == "Failed to connect to /"
                    ) {
                        dispatch(Message.CustomError("Проверьте подключение к интернету"))
                    } else {
                        dispatch(Message.CustomError("Что-то пошло не так =/"))
                    }
                }
            }
        }
    }

}
