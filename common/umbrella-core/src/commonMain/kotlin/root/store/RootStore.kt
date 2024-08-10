package root.store

import com.arkivanov.mvikotlin.core.store.Store
import root.RootComponent
import root.store.RootStore.Intent
import root.store.RootStore.Label
import root.store.RootStore.State

interface RootStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data class HideGreetings(val time: Long = 1000) : Intent
        data class UpdatePermissions(val role: String, val moderation: String, val birthday: String) : Intent
        data object CheckConnection: Intent
        data class ChangeTokenValidationStatus(val isTokenValid: Boolean) : Intent
    }

    data class State (
        val isGreetingsShowing: Boolean,
        val role: String,
        val moderation: String,
        val birthday: String,
        val isTokenValid: Boolean = true
    )

    sealed interface Message {
        data object GreetingsHided : Message
        data class PermissionsUpdated(val role: String, val moderation: String, val birthday: String): Message
        data class TokenValidationStatusChanged(val isTokenValid: Boolean): Message
    }

    sealed interface Label

}
