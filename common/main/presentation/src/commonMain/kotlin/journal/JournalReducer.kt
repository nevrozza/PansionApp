package journal

import com.arkivanov.mvikotlin.core.store.Reducer
import journal.JournalStore.State
import journal.JournalStore.Message

object JournalReducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.StudentsInGroupUpdated -> copy(studentsInGroup = msg.students, currentGroupId = msg.groupId)
            is Message.HeadersUpdated ->  {
                copy(headers = msg.headers, currentModule = msg.currentModule)
            }

            is Message.TeacherGroupsUpdated -> copy(teacherGroups = msg.teacherGroups)
            is Message.ReportCreated -> copy(creatingReportId = msg.id)
            Message.CreatingIdReseted -> copy(creatingReportId = -1)
            Message.ReportDataReseted -> copy(openingReportData = null)
            is Message.ReportDataFetched -> copy(openingReportData = msg.reportData)
            is Message.TimeChanged -> copy(time = msg.time)
            is Message.DateFiltered -> copy(filterDate = msg.date)
            is Message.GroupFiltered -> copy(filterGroupId = msg.groupId)
            is Message.StatusFiltered -> copy(filterStatus = msg.bool)
            is Message.TeacherFiltered -> copy(filterTeacherLogin = msg.teacherLogin)
        }
    }
}