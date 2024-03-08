package mentors

import com.arkivanov.mvikotlin.core.store.Store
import mentors.MentorsStore.Intent
import mentors.MentorsStore.Label
import mentors.MentorsStore.State

interface MentorsStore : Store<Intent, State, Label> {
    object State

    sealed interface Intent

    sealed interface Message

    sealed interface Label

}
