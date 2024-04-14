package allGroupMarks

import com.arkivanov.mvikotlin.core.store.Store
import allGroupMarks.AllGroupMarksStore.Intent
import allGroupMarks.AllGroupMarksStore.Label
import allGroupMarks.AllGroupMarksStore.State
import report.AllGroupMarksStudent

interface AllGroupMarksStore : Store<Intent, State, Label> {
    data class State(
        val groupId: Int,
        val subjectId: Int,
        val subjectName: String,
        val groupName: String,
        val students: List<AllGroupMarksStudent> = emptyList()
    )

    sealed interface Intent {
        data object Init: Intent
    }

    sealed interface Message {
        data class StudentsUpdated(val students: List<AllGroupMarksStudent>) : Message
    }

    sealed interface Label

}
