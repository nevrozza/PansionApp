package journal

import com.arkivanov.mvikotlin.core.store.Store
import journal.JournalStore.Intent
import journal.JournalStore.Label
import journal.JournalStore.State
import journal.init.StudentInGroup

interface JournalStore : Store<Intent, State, Label> {
    data class State(
        val studentsInGroup: List<StudentInGroup> = emptyList(),
        val currentGroupId: Int = 0
    )

    sealed interface Intent {
        data object Init : Intent
        data class OnGroupClicked(val groupId: Int) : Intent
    }

    sealed interface Message {
        data class StudentsInGroupUpdated(val students: List<StudentInGroup>, val groupId: Int) : Message
    }

    sealed interface Label

}
