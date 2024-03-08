package students

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import students.StudentsStore.Intent
import students.StudentsStore.Label
import students.StudentsStore.State
import students.StudentsStore.Message

class StudentsExecutor : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            else -> TODO()
        }
    }
}
