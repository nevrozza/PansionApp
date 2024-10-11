package school

import com.arkivanov.mvikotlin.core.store.Store
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
        val formNum: Int? = null
    )

    sealed interface Intent {
        data object Init: Intent
    }

    sealed interface Message {
        data class Inited(val formId: Int?, val formName: String?, val top: Int?, val formNum: Int?) : Message
    }

    sealed interface Label

}
