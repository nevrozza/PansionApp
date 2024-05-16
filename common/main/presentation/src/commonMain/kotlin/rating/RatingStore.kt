package rating

import FIO
import admin.groups.forms.Form
import admin.schedule.ScheduleGroup
import admin.schedule.ScheduleSubject
import com.arkivanov.mvikotlin.core.store.Store
import rating.RatingStore.Intent
import rating.RatingStore.Label
import rating.RatingStore.State

interface RatingStore : Store<Intent, State, Label> {
    data class State(
        val avatarId: Int,
        val login: String,
        val fio: FIO,
        val currentSubject: Int = -1,
        val me: Map<Int, Pair<Int, Int>?> = hashMapOf(),
        val subjects: List<ScheduleSubject> = listOf(
           startSubject
        ),
        val items: Map<Int, List<RatingItem>> = hashMapOf(),
        val period: Int = 0,
    )

    sealed interface Intent {
        data object Init : Intent
        data class ClickOnSubject(val subjectId: Int) : Intent
    }

    sealed interface Message {
        data class SubjectsUpdated(val subjects: List<ScheduleSubject>) : Message
        data class RatingUpdated(val items: Map<Int, List<RatingItem>>, val me: Map<Int, Pair<Int, Int>?>) : Message
        data class OnSubjectClicked(val subjectId: Int) : Message
    }

    sealed interface Label

}

val startSubject =  ScheduleSubject(
    id = -1,
    name = "Общий рейтинг"
)
