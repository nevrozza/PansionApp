package root.store

import activation.ActivationStore
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import root.store.RootStore.Intent
import root.store.RootStore.Label
import root.store.RootStore.State
import root.store.RootStore.Message

class RootExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {

    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.HideGreetings -> hideGreetings(intent.time)
            is Intent.BottomBarShowing -> dispatch(Message.BottomBarShowingChanged(intent.isShowing))
            is Intent.ChangeCurrentScreen -> dispatch(Message.CurrentScreenChanged(intent.currentCategory, intent.currentScreen))
            is Intent.UpdatePermissions -> dispatch(Message.PermissionsUpdated(intent.role, intent.moderation))
        }
    }

    private fun hideGreetings(time: Long) {
        scope.launch {
            delay(time)
            dispatch(Message.GreetingsHided)
        }
    }
}
