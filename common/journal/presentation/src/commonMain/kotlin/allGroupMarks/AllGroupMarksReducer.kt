package allGroupMarks

import com.arkivanov.mvikotlin.core.store.Reducer
import allGroupMarks.AllGroupMarksStore.State
import allGroupMarks.AllGroupMarksStore.Message

object AllGroupMarksReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.StudentsUpdated -> copy(students = msg.students, firstHalfNums = msg.firstHalfNums)
            is Message.DetailedStupsOpened -> copy(detailedStupsLogin = msg.login)
            is Message.FullReportOpened -> copy(reportData = msg.reportData)
        }
    }
}