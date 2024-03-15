package groups.subjects

import com.arkivanov.mvikotlin.core.store.Reducer
import groups.subjects.SubjectsStore.State
import groups.subjects.SubjectsStore.Message

object SubjectsReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.CDifficultChanged -> copy(cDifficult = msg.difficult)
            is Message.CTeacherLoginChanged -> copy(cTeacherLogin = msg.teacherLogin)
            is Message.CNameChanged -> copy(cName = msg.name)
            is Message.CSubjectTextChanged -> copy(cSubjectText = msg.text)
            is Message.ChosenSubjectChanged -> copy(chosenSubjectId = msg.subjectId)
            is Message.GroupsUpdated -> copy(groups = msg.groups)
        }
    }
}