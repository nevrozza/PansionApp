package parents

import Person
import PersonParent
import admin.groups.forms.CutedForm
import admin.groups.forms.Form
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
        val kids: Map<Int, List<String>> = emptyMap(),
        val forms: List<CutedForm> = emptyList()
    )

    sealed interface Intent {
        data object Init: Intent
        data class EditId(val editId: Int) : Intent
        data class AddToStudent(val login: String) : Intent
        data class PickParent(val login: String) : Intent
        data class CreateChild(val login: String) : Intent
    }

    sealed interface Message {
        data class Inited(val users: List<PersonParent>, val lines: List<ParentLine>, val forms: List<CutedForm>) : Message
        data class KidsUpdated(val kids: Map<Int, List<String>>) : Message
        data class EditId(val editId: Int) : Message
        data class AddToStudent(val login: String) : Message
    }

    sealed interface Label

}
