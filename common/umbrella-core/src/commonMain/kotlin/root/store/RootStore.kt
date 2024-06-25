package root.store

import com.arkivanov.mvikotlin.core.store.Store
import root.RootComponent
import root.store.RootStore.Intent
import root.store.RootStore.Label
import root.store.RootStore.State

interface RootStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data class HideGreetings(val time: Long = 1000) : Intent
        data class BottomBarShowing(val isShowing: Boolean) : Intent
        data class ChangeCurrentScreen(val currentCategory: RootComponent.RootCategories, val currentScreen: RootComponent.Config) : Intent
        data class UpdatePermissions(val role: String, val moderation: String) : Intent
        data object CheckConnection: Intent
        data class ChangeTokenValidationStatus(val isTokenValid: Boolean) : Intent
    }

    data class State (
        val isGreetingsShowing: Boolean,
        val isBottomBarShowing: Boolean,
        val currentScreen: RootComponent.Config,
        val currentCategory: RootComponent.RootCategories = RootComponent.RootCategories.Home,
        val role: String,
        val moderation: String,
        val isTokenValid: Boolean = true
    )

    sealed interface Message {
        data object GreetingsHided : Message
        data class BottomBarShowingChanged(val isShowing: Boolean) : Message
        data class CurrentScreenChanged(val currentCategory: RootComponent.RootCategories, val currentScreen: RootComponent.Config) : Message
        data class PermissionsUpdated(val role: String, val moderation: String): Message
        data class TokenValidationStatusChanged(val isTokenValid: Boolean): Message
    }

    sealed interface Label

}
