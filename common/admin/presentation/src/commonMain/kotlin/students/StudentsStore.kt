package students

import com.arkivanov.mvikotlin.core.store.Store
import students.StudentsStore.Intent
import students.StudentsStore.Label
import students.StudentsStore.State

interface StudentsStore : Store<Intent, State, Label> {
    object State

    sealed interface Intent

    sealed interface Message

    sealed interface Label

}
