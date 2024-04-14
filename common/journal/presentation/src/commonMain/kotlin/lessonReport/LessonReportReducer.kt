package lessonReport

import com.arkivanov.mvikotlin.core.store.Reducer

object LessonReportReducer : Reducer<LessonReportStore.State, LessonReportStore.Message> {
    override fun LessonReportStore.State.reduce(msg: LessonReportStore.Message): LessonReportStore.State {
        return when (msg) {
            is LessonReportStore.Message.ColumnsUpdated -> copy(columnNames = msg.columns, deletingReportColumn = null)
            is LessonReportStore.Message.DeleteColumnInited -> copy(deletingReportColumn = msg.reportColumn)
            is LessonReportStore.Message.MarksMenuOpened -> copy(selectedLogin = msg.studentLogin, selectedMarkReason = msg.reasonId, selectedMarkValue = msg.markValue)
            LessonReportStore.Message.SelectionCleared -> copy(selectedLogin = "", selectedMarkReason = "", selectedMarkValue = "")
            is LessonReportStore.Message.StudentsUpdated -> copy(students = msg.students, ids = ids+1)
            is LessonReportStore.Message.SettingsTabChanged -> copy(settingsTab = msg.settingsTab)
            is LessonReportStore.Message.TopicChanged -> copy(topic = msg.topic)
            is LessonReportStore.Message.DescriptionChanged -> copy(description = msg.description)
            is LessonReportStore.Message.RepUpdated -> copy(likedList = msg.likedList, dislikedList = msg.dislikedList)
            is LessonReportStore.Message.LateTimeMenuOpened -> {"sx: ${msg.studentLogin}"; copy(selectedLogin = msg.studentLogin)}
            LessonReportStore.Message.InfoShowingChanged -> copy(isInfoShowing = !isInfoShowing)
            is LessonReportStore.Message.StatusChanged -> copy(status = msg.status)
            LessonReportStore.Message.IsMentorWasChanged -> copy(isMentorWas = !isMentorWas)
            is LessonReportStore.Message.EditTimeChanged -> copy(editTime = msg.editTime)
//            is LessonReportStore.Message.isFABShowing -> copy(isFabShowing = msg.isShowing)
//            is LessonReportStore.Message.HeaderUpdated -> with(msg) {
//                copy(
//                    lessonReportId = header.reportId,
//                    subjectName = header.subjectName,
//                    groupName = header.groupName,
//                    groupId = header.groupId,
//                    teacherName = header.teacherName,
//                    date = header.date,
//                    time = header.time,
//                    status = header.status,
//                    ids = header.ids,
//                    isMentorWas = header.isMentorWas
//                )
//            }
            is LessonReportStore.Message.Inited -> copy(students = msg.students, likedList = msg.likedList, dislikedList = msg.dislikedList)
            is LessonReportStore.Message.DetailedMarksFetched -> copy(detailedMarks = msg.marks)
            is LessonReportStore.Message.DetailedMarksOpened -> copy(detailedMarksLogin = msg.login, detailedMarks = emptyList())
        }
    }
}