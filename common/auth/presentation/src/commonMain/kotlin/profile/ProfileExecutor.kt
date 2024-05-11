package profile

import AuthRepository
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import profile.ProfileStore.Intent
import profile.ProfileStore.Label
import profile.ProfileStore.State
import profile.ProfileStore.Message

class ProfileExecutor(
    private val authRepository: AuthRepository
) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            else -> TODO()
        }
    }
}
