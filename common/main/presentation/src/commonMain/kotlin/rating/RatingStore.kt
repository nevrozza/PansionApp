package rating

import FIO
import admin.groups.forms.Form
import admin.schedule.ScheduleGroup
import admin.schedule.ScheduleSubject
import com.arkivanov.mvikotlin.core.store.Store
import rating.RatingStore.Intent
import rating.RatingStore.Label
import rating.RatingStore.State
import server.ExtraSubjectsId

interface RatingStore : Store<Intent, State, Label> {
    data class State(
        val avatarId: Int,
        val login: String,
        val fio: FIO,
        val currentSubject: Int = -1,
        val me: Map<Int, Pair<Int, Int>?> = hashMapOf(),
        val subjects: List<ScheduleSubject> = listOf(
           startSubject,
            mvdSubject
        ),
        val lastEditTime: String = "",
        val items: Map<Int, List<RatingItem>> = hashMapOf(),
        val period: Int = 0, // Week, Module, Year
        val forms: Int = 0, //All, 5-8, 9-11
    )

    sealed interface Intent {
        data object Init : Intent
        data class ClickOnSubject(val subjectId: Int) : Intent
        data class ClickOnForm(val formNum: Int) : Intent
        data class ClickOnPeriod(val period: Int) : Intent
    }

    sealed interface Message {
        data class SubjectsUpdated(val subjects: List<ScheduleSubject>) : Message
        data class RatingUpdated(val items: Map<Int, List<RatingItem>>, val me: Map<Int, Pair<Int, Int>?>, val lastEditTime: String) : Message
        data class OnSubjectClicked(val subjectId: Int) : Message
        data class OnFormClicked(val formNum: Int) : Message
        data class OnPeriodClicked(val period: Int) : Message
    }

    sealed interface Label

}

val startSubject =  ScheduleSubject(
    id = ExtraSubjectsId.common,
    name = "Общий рейтинг",
    isActive = true
)
val mvdSubject =  ScheduleSubject(
    id = ExtraSubjectsId.mvd,
    name = "Дисциплина",
    isActive = true
)

val socialWorkSubject =  ScheduleSubject(
    id = ExtraSubjectsId.social,
    name = "Общественная работа",
    isActive = true
)

val creativeSubject =  ScheduleSubject(
    id = ExtraSubjectsId.creative,
    name = "Творчество",
    isActive = true
)
