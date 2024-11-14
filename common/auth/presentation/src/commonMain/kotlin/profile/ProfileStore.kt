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
        val isOwner: Boolean,
        val isCanEdit: Boolean,
        val studentLogin: String,
        val fio: FIO,
        val avatarId: Int,
        val newAvatarId: Int = avatarId,
        val tabIndex: Int = 0,
        val groups: List<Group> = emptyList(),
        val subjects: List<Subject> = emptyList(),
        val teachers: HashMap<String, String> = hashMapOf(),
        val form: Form? = null,
        val likes: Int = 0,
        val dislikes: Int = 0,
        val giaSubjects: List<Int> = emptyList(),
        val ministryId: String = "0",
        val ministryLvl: String = "0",

        val pansCoins: Int = 0,
        val avatars: List<Int>? = null
    )

    sealed interface Intent {
        data class ChangeTab(val index: Int) : Intent
        data class SetNewAvatarId(val avatarId: Int) : Intent
        data class SaveAvatarId(val avatarId: Int, val price: Int) : Intent

        data class ClickOnGIASubject(val subjectId: Int, val isChecked: Boolean) : Intent

        data object Init : Intent
    }

    sealed interface Message {
        data class TabChanged(val index: Int) : Message
        data class NewAvatarIdChanged(val avatarId: Int) : Message

        data class AvatarIdSaved(val price: Int, val avatarId: Int) : Message


        data class GIASubjectsUpdated(val giaSubjects: List<Int>) : Message
        data class AboutMeUpdated(
            val giaSubjects: List<Int>,
            val likes: Int,
            val dislikes: Int,
            val form: Form,
            val groups: List<Group>,
            val subjects: List<Subject>,
            val teachers: HashMap<String, String>,

            val ministryId: String,
            val ministryLvl: String,

            val pansCoins: Int,
            val avatars: List<Int>
        ) : Message
    }

    sealed interface Label

}
