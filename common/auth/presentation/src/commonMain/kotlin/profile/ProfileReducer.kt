package profile

import com.arkivanov.mvikotlin.core.store.Reducer
import profile.ProfileStore.State
import profile.ProfileStore.Message

object ProfileReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.TabChanged -> copy(tabIndex = msg.index)
            is Message.NewAvatarIdChanged -> copy(newAvatarId = msg.avatarId)
            Message.AvatarIdSaved -> copy(avatarId = newAvatarId)
            is Message.AboutMeUpdated -> copy(
                groups = msg.groups,
                subjects = msg.subjects,
                teachers = msg.teachers,
                form = msg.form,
                likes = msg.likes,
                dislikes = msg.dislikes,
                giaSubjects = msg.giaSubjects,
                ministryId = msg.ministryId,
                ministryLvl = msg.ministryLvl
            )

            is Message.GIASubjectsUpdated -> copy(giaSubjects = msg.giaSubjects)
        }
    }
}