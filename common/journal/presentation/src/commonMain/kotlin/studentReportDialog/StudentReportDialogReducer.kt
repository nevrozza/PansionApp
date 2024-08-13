package studentReportDialog

import com.arkivanov.mvikotlin.core.store.Reducer
import studentReportDialog.StudentReportDialogStore.State
import studentReportDialog.StudentReportDialogStore.Message

object StudentReportDialogReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            Message.DialogClosed -> StudentReportDialogStore.State()
            is Message.DialogOpened -> copy(
                marks = msg.marks,
                stups = msg.stups,
                studentLine = msg.studentLine,
                info = msg.info,
                homeTasks = msg.homeTasks
            )
        }
    }
}