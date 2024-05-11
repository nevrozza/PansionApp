package profile

import FIO
import com.arkivanov.mvikotlin.core.store.Store
import profile.ProfileStore.Intent
import profile.ProfileStore.Label
import profile.ProfileStore.State

interface ProfileStore : Store<Intent, State, Label> {
    data class State(
        val fio: FIO
    )

    sealed interface Intent

    sealed interface Message

    sealed interface Label

}
