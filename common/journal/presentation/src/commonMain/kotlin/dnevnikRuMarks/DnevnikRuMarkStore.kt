package dnevnikRuMarks

import com.arkivanov.mvikotlin.core.store.Store
import dnevnikRuMarks.DnevnikRuMarkStore.Intent
import dnevnikRuMarks.DnevnikRuMarkStore.Label
import dnevnikRuMarks.DnevnikRuMarkStore.State
import kotlinx.serialization.Serializable
import report.DnevnikRuMarksSubject
import report.ServerRatingUnit
import report.UserMark

interface DnevnikRuMarkStore : Store<Intent, State, Label> {
    data class State(
        val studentLogin: String,
        val subjects: HashMap<Int, List<DnevnikRuMarksSubject>> = hashMapOf(),
        val isQuarters: Boolean? = null,
        val tabIndex: Int? = null,
        val tabsCount: Int = 0,

        val pickedSubjectId: Int = 0
    )

    sealed interface Intent {
        data object Init: Intent
        data class ClickOnTab(val index: Int) : Intent

        data class ClickOnStupsSubject(val id: Int) : Intent
    }

    sealed interface Message {
        data class SubjectsUpdated(val subjects: List<DnevnikRuMarksSubject>) : Message
        data class IsQuartersInited(val isQuarters: Boolean, val tabIndex: Int, val tabsCount: Int) : Message
        data class OnTabClicked(val index: Int) : Message
        data class OnStupsSubjectClicked(val id: Int) : Message
    }

    sealed interface Label

}

