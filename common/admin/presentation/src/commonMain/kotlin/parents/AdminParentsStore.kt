package parents

import com.arkivanov.mvikotlin.core.store.Store
import parents.AdminParentsStore.Intent
import parents.AdminParentsStore.Label
import parents.AdminParentsStore.State

interface AdminParentsStore : Store<Intent, State, Label> {
    data class State(
        val a: String = ""
    )

    sealed interface Intent {
        data object Init: Intent
    }

    sealed interface Message

    sealed interface Label

}
