package profile

import FIO
import com.arkivanov.mvikotlin.core.store.Store
import profile.ProfileStore.Intent
import profile.ProfileStore.Label
import profile.ProfileStore.State

interface ProfileStore : Store<Intent, State, Label> {
    data class State(
        val studentLogin: String,
        val fio: FIO,
        val avatarId: Int,
        val newAvatarId: Int = avatarId,
        val tabIndex: Int = 0
    )

    sealed interface Intent {
        data class ChangeTab(val index: Int) : Intent
        data class SetNewAvatarId(val avatarId: Int) : Intent

        data object SaveAvatarId : Intent
    }

    sealed interface Message {
        data class TabChanged(val index: Int) : Message
        data class NewAvatarIdChanged(val avatarId: Int) : Message

        data object AvatarIdSaved : Message
    }

    sealed interface Label

}
