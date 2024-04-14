package dnevnikRuMarks

import com.arkivanov.mvikotlin.core.store.Store
import dnevnikRuMarks.DnevnikRuMarkStore.Intent
import dnevnikRuMarks.DnevnikRuMarkStore.Label
import dnevnikRuMarks.DnevnikRuMarkStore.State
import kotlinx.serialization.Serializable
import report.DnevnikRuMarksSubject
import report.ServerRatingUnit

interface DnevnikRuMarkStore : Store<Intent, State, Label> {
    data class State(
        val studentLogin: String,
        val subjects: List<DnevnikRuMarksSubject> = emptyList(),
        val isQuarters: Boolean? = null,
        val tabIndex: Int? = null
    )

    sealed interface Intent {
        data object Init: Intent
        data class ClickOnTab(val index: Int) : Intent
    }

    sealed interface Message {
        data class SubjectsUpdated(val subjects: List<DnevnikRuMarksSubject>) : Message
        data class IsQuartersInited(val isQuarters: Boolean, val tabIndex: Int) : Message
        data class OnTabClicked(val index: Int) : Message
    }

    sealed interface Label

}

