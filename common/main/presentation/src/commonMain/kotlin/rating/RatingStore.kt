package rating

import FIO
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
        val me: Map<String, Map<Int, RatingItem?>> = hashMapOf(),
        val subjects: List<ScheduleSubject> = listOf(
           startSubject,
            mvdSubject
        ),
        val lastEditTime: String = "",
        val items: Map<String, Map<Int, List<RatingItem>>> = hashMapOf(),
        val period: PansionPeriod? = null, // Week, Module, Year
        val forms: Int = 0, //All, 5-8, 9-11
        val isDetailed: Boolean = false
    )

    sealed interface Intent {
        data object ChangeIsDetailed : Intent
        data object Init : Intent
        data class ClickOnSubject(val subjectId: Int) : Intent
        data class ClickOnForm(val formNum: Int) : Intent
        data class ClickOnPeriod(val period: String) : Intent
    }

    sealed interface Message {
        data object IsDetailedChanged : Message
        data class SubjectsUpdated(val subjects: List<ScheduleSubject>, val currentPeriod: PansionPeriod) : Message
        data class RatingUpdated(val items: Map<String, Map<Int, List<RatingItem>>>, val me: Map<String, Map<Int, RatingItem?>>, val lastEditTime: String) : Message
        data class OnSubjectClicked(val subjectId: Int) : Message
        data class OnFormClicked(val formNum: Int) : Message
        data class OnPeriodClicked(val period: PansionPeriod) : Message
    }

    sealed interface Label

}

val startSubject =  ScheduleSubject(
    id = ExtraSubjectsId.COMMON,
    name = "Общий рейтинг",
    isActive = true
)
val mvdSubject =  ScheduleSubject(
    id = ExtraSubjectsId.MVD,
    name = "Дисциплина",
    isActive = true
)

val zdravoohrSubject =  ScheduleSubject(
    id = ExtraSubjectsId.ZDRAV,
    name = "Здравоохранение",
    isActive = true
)

val socialWorkSubject =  ScheduleSubject(
    id = ExtraSubjectsId.SOCIAL,
    name = "Общественная работа",
    isActive = true
)

val creativeSubject =  ScheduleSubject(
    id = ExtraSubjectsId.CREATIVE,
    name = "Творчество",
    isActive = true
)
