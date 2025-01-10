package detailedStups

import com.arkivanov.mvikotlin.core.store.Store
import detailedStups.DetailedStupsStore.Intent
import detailedStups.DetailedStupsStore.Label
import detailedStups.DetailedStupsStore.State
import report.DetailedStupsSubject
import server.getCurrentEdYear
import server.getWeekDays

interface DetailedStupsStore : Store<Intent, State, Label> {
    data class State(
        val login: String,
        val reason: String,
        val weekDays: List<String> = getWeekDays(),
        val subjects: List<DetailedStupsSubject> = emptyList(),
        val edYear: Int = getCurrentEdYear(),
        val name: String,
        val avatarId: Int,
    )

    sealed interface Intent {
        data object Init : Intent
        data object ChangeReason : Intent
    }

    sealed interface Message {
        data class SubjectsUpdated(val subjects: List<DetailedStupsSubject>) : Message
        data object ReasonChanged : Message
    }

    sealed interface Label

}
