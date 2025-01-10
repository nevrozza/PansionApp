package achievements

import Person
import com.arkivanov.mvikotlin.core.store.Store
import achievements.AdminAchievementsStore.Intent
import achievements.AdminAchievementsStore.Label
import achievements.AdminAchievementsStore.State
import server.getCurrentEdYear

interface AdminAchievementsStore : Store<Intent, State, Label> {
    data class State(
        val achievements: List<AchievementsDTO> = emptyList(),
        val students: List<Person> = emptyList(),
        val subjects: Map<Int, String> = emptyMap(),
        val edYear: Int = getCurrentEdYear(),

        val bsId: Int? = null,
        val bsStudentLogin: String = "",
        val bsDate: String = "",
        val bsText: String = "",
        val bsShowDate: String = "",
        val bsSubjectId: Int? = null,
        val bsStups: Int = 0,
        val bsOldDate: String = "",
        val bsOldText: String = "",
        val bsOldShowDate: String = "",
    )

    sealed interface Intent {
        data object Init : Intent
        data object OpenCreateBS : Intent
        data class OpenAddBS(val date: String, val showDate: String, val text: String, val subjectId: Int, val stups: Int) : Intent
        data class OpenHugeBS(val text: String, val date: String, val showDate: String, val oldText: String, val oldDate: String, val oldShowDate: String) : Intent
        data class OpenEditBS(val id: Int, val studentLogin: String, val subjectId: Int, val stups: Int, val text: String, val date: String) : Intent

        data object CreateAchievement : Intent
        data object EditAchievement : Intent
        data object DeleteAchievement : Intent
        data object UpdateGroupAchievement : Intent
        data class ChangeStudentLogin(val login: String) : Intent
        data class ChangeDate(val date: String) : Intent
        data class ChangeShowDate(val date: String) : Intent
        data class ChangeText(val text: String) : Intent
        data class ChangeSubjectId(val id: Int) : Intent
        data class ChangeStups(val stups: Int) : Intent
    }

    sealed interface Message {
        data class Inited(val achievements: List<AchievementsDTO>, val students: List<Person>, val subjects: Map<Int, String>) : Message
        data class BSInit(val id: Int?, val studentLogin: String, val date: String, val text: String, val showDate: String, val subjectId: Int?, val stups: Int, val oldText: String = "", val oldDate: String = "", val oldShowDate: String = "") : Message

        data class StudentLoginChanged(val login: String) : Message
        data class DateChanged(val date: String) : Message
        data class ShowDateChanged(val date: String) : Message
        data class TextChanged(val text: String) : Message
        data class SubjectIdChanged(val id: Int) : Message
        data class StupsChanged(val stups: Int) : Message
    }

    sealed interface Label

}
