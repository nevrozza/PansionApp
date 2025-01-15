package formRating

import admin.groups.forms.CutedForm
import com.arkivanov.mvikotlin.core.store.Store
import formRating.FormRatingStore.Intent
import formRating.FormRatingStore.Label
import formRating.FormRatingStore.State
import rating.FormRatingStudent
import rating.PansionPeriod


// 0 - week
// 1 - prevWeek
// 2 - module
// 3 - halfYear
// 4 - year

data class FormRatingPage(
    val period: PansionPeriod,
    val formId: Int,
    val students: List<FormRatingStudent>
)

interface FormRatingStore : Store<Intent, State, Label> {
    data class State(
        val login: String,
        val role: String,
        val formId: Int?,
        val formNum: Int?,
        val formName: String?,
        val period: PansionPeriod? = null,
        val availableForms: List<CutedForm> = emptyList(),
        val formRatingPages: List<FormRatingPage> = emptyList(),

        val stupsLogin: String = "",
        val subjects: Map<Int, String> = emptyMap(),

        val isDetailed: Boolean = false
    )

    sealed interface Intent {
        data object Init : Intent
        data class ChangeForm(
            val formId: Int
        ) : Intent

        data class ChangePeriod(
            val period: PansionPeriod
        ) : Intent

        data class SelectStupsLogin(
            val login: String
        ) : Intent

        data object ChangeIsDetailed: Intent
    }

    sealed interface Message {

        data object IsDetailedChanged: Message
        data class AvailableFormsUpdated(val availableForms: List<CutedForm>) : Message
        data class FormChanged(val formId: Int, val formNum: Int, val formName: String) : Message
        data class PeriodChanged(val period: PansionPeriod) : Message
        data class FormRatingPagesUpdated(val pages: List<FormRatingPage>, val subjects: Map<Int, String>) : Message
        data class StupsLoginSelected(val login: String) : Message
    }

    sealed interface Label

}
