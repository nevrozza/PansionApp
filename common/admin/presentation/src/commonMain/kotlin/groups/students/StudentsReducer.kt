package groups.students

import com.arkivanov.mvikotlin.core.store.Reducer
import groups.students.StudentsStore.State
import groups.students.StudentsStore.Message

object StudentsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.ChosenFormChanged -> copy(chosenFormTabId = msg.formId)
            is Message.OnStudentClicked -> copy(chosenStudentLogin = msg.studentLogin)
            is Message.OnStudentPlusClicked -> copy(chosenStudentPlusLogin = msg.studentLogin)
            is Message.StudentsUpdated -> copy(studentsInForm = msg.students)
            is Message.StudentGroupsUpdated -> copy(studentGroups = msg.studentGroups)
        }
    }
}