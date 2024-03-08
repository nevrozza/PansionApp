package mentors

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import mentors.MentorsStore.Intent
import mentors.MentorsStore.Label
import mentors.MentorsStore.State
import mentors.MentorsStore.Message

class MentorsExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            else -> TODO()
        }
    }
}
