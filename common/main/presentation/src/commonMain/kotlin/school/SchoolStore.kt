package school

import com.arkivanov.mvikotlin.core.store.Store
import main.school.MinistryStudent
import school.SchoolStore.Intent
import school.SchoolStore.Label
import school.SchoolStore.State

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

        val ministryId: String = "0"
    )

    sealed interface Intent {
        data object Init: Intent
        data object OpenMinistrySettings: Intent
        data class SetMinistryStudent(val ministryId: String, val login: String?, val fio: String): Intent
    }

    sealed interface Message {
        data class Inited(val formId: Int?, val formName: String?, val top: Int?, val formNum: Int?, val ministryId: String) : Message
        data class MinistrySettingsOpened(val ministryStudents: List<MinistryStudent>) : Message
    }

    sealed interface Label

}
