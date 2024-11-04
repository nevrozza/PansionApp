package school

import PersonPlus
import com.arkivanov.mvikotlin.core.store.Store
import main.school.*
import school.SchoolStore.Intent
import school.SchoolStore.Label
import school.SchoolStore.State
import server.getCurrentDate
import server.getDates


interface SchoolStore : Store<Intent, State, Label> {
    data class State(
        val login: String,
        val moderation: String,
        val role: String,
        val formId: Int? = null,
        val formName: String? = null,
        val top: Int? = null,
        val formNum: Int? = null,

        val ministryStudents: List<MinistryStudent> = emptyList(),
        val ministrySettingsReason: MinistrySettingsReason = MinistrySettingsReason.Overview,

        val ministryId: String = "0",
        val mvdStupsCount: Int = 0,
        val zdStupsCount: Int = 0,

        val dutyKids: List<DutyKid> = emptyList(),
        val dutyPeopleCount: Int = 2,

        val ministryOverviewId: String = "0",
        val dates: List<Pair<Int, String>> = getDates(minus = 14, plus = 0).mapNotNull { if (it.first in listOf(6, 7)) null else it },
        val currentDate: Pair<Int, String> = getCurrentDate(),

        val ministryList: List<MinistryListItem> = emptyList(),
    )

    sealed interface Intent {
        data class ChangeDate(val date: Pair<Int, String>) : Intent
        data class OpenMinistryOverview(val ministryOverviewId: String) : Intent
        data object Init: Intent
        data class OpenMinistrySettings(val reason: MinistrySettingsReason): Intent
        data class SetMinistryStudent(val ministryId: String, val login: String?, val fio: String): Intent

        data class StartNewDayDuty(val newDutyPeopleCount: Int) : Intent
        data class UpdateTodayDuty(val kids: List<String>, val newDutyPeopleCount: Int) : Intent
    }

    sealed interface Message {

        data class DateChanged(val date: Pair<Int, String>) : Message
        data class Inited(val formId: Int?, val formName: String?, val top: Int?, val formNum: Int?, val ministryId: String, val mvdStupsCount: Int, val zdStupsCount: Int) : Message
        data class MinistrySettingsOpened(val ministryStudents: List<MinistryStudent>) : Message
        data class DutyFetched(val dutyKids: List<DutyKid>, val dutyPeopleCount: Int) : Message
        data class MinistrySettingsReasonChanged(val reason: MinistrySettingsReason) : Message

        data class MinistryOverviewOpened(val ministryOverviewId: String) : Message
        data class MinistryListUpdated(val list: List<MinistryListItem>) : Message
    }

    sealed interface Label

}
