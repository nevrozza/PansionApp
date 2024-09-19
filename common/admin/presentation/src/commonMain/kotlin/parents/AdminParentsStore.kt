package parents

import Person
import PersonParent
import admin.parents.ParentLine
import com.arkivanov.mvikotlin.core.store.Store
import parents.AdminParentsStore.Intent
import parents.AdminParentsStore.Label
import parents.AdminParentsStore.State

interface AdminParentsStore : Store<Intent, State, Label> {
    data class State(
        val users: List<PersonParent> = emptyList(),
        val lines: List<ParentLine> = emptyList(),
        val editId: Int = 0,
        val addToStudent: String = "",
        val kids: List<String> = emptyList(),
    )

    sealed interface Intent {
        data object Init: Intent
        data class EditId(val editId: Int) : Intent
        data class AddToStudent(val login: String) : Intent
        data class PickParent(val login: String) : Intent
        data class CreateChild(val login: String) : Intent
    }

    sealed interface Message {
        data class Inited(val users: List<PersonParent>, val lines: List<ParentLine>) : Message
        data class KidsUpdated(val kids: List<String>) : Message
        data class EditId(val editId: Int) : Message
        data class AddToStudent(val login: String) : Message
    }

    sealed interface Label

}
