package studentLines

import com.arkivanov.mvikotlin.core.store.Store
import report.ClientStudentLine
import server.getCurrentEdYear
import studentLines.StudentLinesStore.Intent
import studentLines.StudentLinesStore.Label
import studentLines.StudentLinesStore.State

interface StudentLinesStore : Store<Intent, State, Label> {
    data class State(
        val studentLines: List<ClientStudentLine> = emptyList(),
        val edYear: Int = getCurrentEdYear(),
        val login: String
    )

    sealed interface Intent {
        data object Init: Intent
    }

    sealed interface Message {
        data class StudentLinesInited(val studentLines: List<ClientStudentLine>) : Message
    }

    sealed interface Label

}
