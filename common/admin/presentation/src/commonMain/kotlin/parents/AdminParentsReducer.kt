package parents

import admin.groups.forms.formSort
import com.arkivanov.mvikotlin.core.store.Reducer
import parents.AdminParentsStore.State
import parents.AdminParentsStore.Message

object AdminParentsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.Inited -> copy(users = msg.users, lines = msg.lines, forms = msg.forms.formSort())
            is Message.EditId -> copy(editId = msg.editId, addToStudent = "")
            is Message.AddToStudent -> copy(editId = 0, addToStudent = msg.login)
            is Message.KidsUpdated -> copy(kids = msg.kids)
        }
    }
}