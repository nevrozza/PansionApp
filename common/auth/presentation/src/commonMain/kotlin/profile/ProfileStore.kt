package profile

import FIO
import admin.groups.Group
import admin.groups.Subject
import admin.groups.forms.Form
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
        val tabIndex: Int = 0,
        val groups: List<Group> = emptyList(),
        val subjects: List<Subject> = emptyList(),
        val teachers: HashMap<String, String> = hashMapOf(),
        val form: Form? = null
    )

    sealed interface Intent {
        data class ChangeTab(val index: Int) : Intent
        data class SetNewAvatarId(val avatarId: Int) : Intent
        data object SaveAvatarId : Intent

        data object Init : Intent
    }

    sealed interface Message {
        data class TabChanged(val index: Int) : Message
        data class NewAvatarIdChanged(val avatarId: Int) : Message

        data object AvatarIdSaved : Message

        data class AboutMeUpdated(val form: Form, val groups: List<Group>, val subjects: List<Subject>, val teachers: HashMap<String, String>) : Message
    }

    sealed interface Label

}
