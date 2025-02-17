package root.store

import com.arkivanov.mvikotlin.core.store.Reducer
import root.store.RootStore.State
import root.store.RootStore.Message

object RootReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            Message.GreetingsHided -> copy(isGreetingsShowing = false)
            is Message.PermissionsUpdated -> copy(role = msg.role, moderation = msg.moderation, birthday = msg.birthday)
            is Message.TokenValidationStatusChanged -> copy(isTokenValid = msg.isTokenValid)
            is Message.VersionFetched -> copy(version = msg.version)
            is Message.StartFetched -> copy(
                startRouting = msg.routing,
                startUser = msg.rUser,
                startGroup = msg.rGroup,
                startReport = msg.rReportData,
                isStartUserGreetingsShowing = false
            )

            Message.StartIsNeeded -> copy(isStartUserGreetingsShowing = true)
        }
    }
}