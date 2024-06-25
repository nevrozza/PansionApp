package root.store

import AuthRepository
import CDispatcher
import activation.ActivationStore
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import home.HomeComponent
import home.HomeStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import root.store.RootStore.Intent
import root.store.RootStore.Label
import root.store.RootStore.State
import root.store.RootStore.Message

class RootExecutor(
    val authRepository: AuthRepository,
    val checkNInterface: NetworkInterface,
    private val gotoHome: () -> Unit
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {

    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.HideGreetings -> hideGreetings(intent.time)
            is Intent.BottomBarShowing -> scope.launch {
                dispatch(
                    Message.BottomBarShowingChanged(
                        intent.isShowing
                    )
                )
            }

            is Intent.ChangeCurrentScreen -> scope.launch {
                dispatch(
                    Message.CurrentScreenChanged(
                        intent.currentCategory,
                        intent.currentScreen
                    )
                )
            }

            is Intent.UpdatePermissions -> updatePermissions(
                intent.role,
                intent.moderation
            )

            Intent.CheckConnection -> checkConnection()
            is Intent.ChangeTokenValidationStatus -> dispatch(Message.TokenValidationStatusChanged(intent.isTokenValid))
        }
    }

    private fun updatePermissions(role: String, moderation: String) {
        scope.launch {
            dispatch(
                Message.PermissionsUpdated(
                    role, moderation
                )
            )
        }
    }

    private fun checkConnection() {
        checkNInterface.nStartLoading()
        scope.launch(CDispatcher) {
            try {
                val r = authRepository.checkConnection()


                scope.launch {
                    checkNInterface.nSuccess()
                    if (r.isTokenValid) {
                        updatePermissions(r.role, r.moderation)
                        authRepository.updateAfterFetch(r)
                        gotoHome()
                    } else {
                        dispatch(Message.TokenValidationStatusChanged(false))
                    }
                }

            } catch (_: Throwable) {
                checkNInterface.nError("Не удалось подключиться к серверу") {
                    checkConnection()
                }
            }
        }
    }

    private fun hideGreetings(time: Long) {
        scope.launch {
            delay(time)
            dispatch(Message.GreetingsHided)
        }
    }
}
