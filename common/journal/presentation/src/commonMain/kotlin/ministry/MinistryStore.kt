package ministry

import admin.groups.forms.Form
import com.arkivanov.mvikotlin.core.store.Store
import main.school.MinistryKid
import main.school.MinistryListItem
import ministry.MinistryStore.Intent
import ministry.MinistryStore.Label
import ministry.MinistryStore.State
import server.getCurrentDate
import server.getCurrentEdYear
import server.getDates

interface MinistryStore : Store<Intent, State, Label> {
    data class State(
        val dates: List<Pair<Int, String>> = getDates(minus = 14, plus = 0).mapNotNull { if (it.first in listOf(6, 7)) null else it },
        val currentDate: Pair<Int, String> = getCurrentDate(),
        val isMultiMinistry: Boolean? = null,
        val pickedMinistry: String = "0",
        val ministryList: List<MinistryListItem> = emptyList(),
        val mvdLogin: String = "",
        val mvdReportId: Int? = null,
        val mvdCustom: String = "",
        val mvdStups: Int = 0,
        val pickedFormId: Int? = null,
        val forms: List<Form> = emptyList()
    )

    sealed interface Intent {
        data class PickFormId(val formId: Int) : Intent
        data object Init : Intent
        data class ChangeMinistry(val ministryId: String) : Intent
        data class ChangeDate(val date: Pair<Int, String>) : Intent

        data class OpenMVDEdit(
            val login: String,
            val reason: String,
            val reportId: Int?,
            val custom: String,
            val stups: Int
        ) : Intent

        data class ChangeDs3Stepper(val stups: Int) : Intent
        data class ChangeDs3Custom(val custom: String) : Intent

        data class UploadStup(
            val reason: String,
            val formId: Int,
            val login: String,
            val content: String,
            val reportId: Int?,
            val custom: String?
        ) : Intent
    }

    sealed interface Message {
        data class FormIdPicked(val formId: Int) : Message
        data class MinistryHeaderInited(val isMultiMinistry: Boolean, val pickedMinistry: String) : Message
        data class MinistryChanged(val ministryId: String) : Message

        data class DateChanged(val date: Pair<Int, String>) : Message
        data class ListUpdated(val list: List<MinistryListItem>) : Message

        data class MVDDS3Opened(val custom: String, val stups: Int) : Message

        data class MVDEditOpened(val login: String, val reportId: Int?) : Message
        data class Ds3StepperChanged(val stups: Int) : Message
        data class Ds3CustomChanged(val custom: String) : Message
        data class FormFetched(val forms: List<Form>) : Message
    }

    sealed interface Label

}
