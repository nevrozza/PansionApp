package lessonReport

import com.arkivanov.mvikotlin.core.store.Reducer

object LessonReportReducer : Reducer<LessonReportStore.State, LessonReportStore.Message> {
    override fun LessonReportStore.State.reduce(msg: LessonReportStore.Message): LessonReportStore.State {
        return when (msg) {
            is LessonReportStore.Message.ColumnsUpdated -> copy(
                columnNames = msg.columns,
                deletingReportColumn = null,
                isUpdateNeeded = true
            )

            is LessonReportStore.Message.DeleteColumnInited -> copy(deletingReportColumn = msg.reportColumn)
            is LessonReportStore.Message.MarksMenuOpened -> copy(
                selectedLogin = msg.studentLogin,
                selectedMarkReason = msg.reasonId,
                selectedMarkValue = msg.markValue,
                selectedDeploy = msg.selectedDeploy
            )

            LessonReportStore.Message.SelectionCleared -> copy(
                selectedLogin = "",
                selectedMarkReason = "",
                selectedMarkValue = "",
                selectedDeploy = ""
            )

            is LessonReportStore.Message.StudentsUpdated -> copy(
                students = msg.students,
                ids = ids + 1,
                isUpdateNeeded = true
            )

            is LessonReportStore.Message.SettingsTabChanged -> copy(settingsTab = msg.settingsTab)
            is LessonReportStore.Message.TopicChanged -> copy(
                topic = msg.topic,
                isUpdateNeeded = true
            )

            is LessonReportStore.Message.DescriptionChanged -> copy(
                description = msg.description,
                isUpdateNeeded = true
            )

            is LessonReportStore.Message.RepUpdated -> copy(
                likedList = msg.likedList,
                dislikedList = msg.dislikedList,
                isUpdateNeeded = true
            )

            is LessonReportStore.Message.LateTimeMenuOpened -> {
                "sx: ${msg.studentLogin}"; copy(selectedLogin = msg.studentLogin)
            }

            LessonReportStore.Message.InfoShowingChanged -> copy(isInfoShowing = !isInfoShowing)
            is LessonReportStore.Message.StatusChanged -> copy(
                status = msg.status,
                isUpdateNeeded = true
            )

            LessonReportStore.Message.IsMentorWasChanged -> copy(
                isMentorWas = !isMentorWas,
                isUpdateNeeded = true
            )

            is LessonReportStore.Message.EditTimeChanged -> copy(
                editTime = msg.editTime,
                isUpdateNeeded = false
            )

            is LessonReportStore.Message.Inited -> copy(
                students = msg.students,
                likedList = msg.likedList,
                dislikedList = msg.dislikedList,
                isUpdateNeeded = false,
                columnNames = if(columnNames.none { it.type == "!cl5" }) (columnNames + ReportColumn(title = "clРабота на уроке", type = "!cl5")).sortedBy { customOrder[it.type] } else columnNames
            )

            is LessonReportStore.Message.DetailedMarksFetched -> copy(detailedMarks = msg.marks)
            is LessonReportStore.Message.DetailedMarksOpened -> copy(
                detailedMarksLogin = msg.login,
                detailedMarks = emptyList()
            )

            is LessonReportStore.Message.IsSavedAnimation -> copy(isSavedAnimation = msg.isSaved)
            is LessonReportStore.Message.IsErrorAnimation -> copy(isErrorAnimation = msg.isError)
            is LessonReportStore.Message.HomeTasksUpdated -> copy(hometasks = msg.homeTasks)
            is LessonReportStore.Message.IsHomeTasksErrorAnimation -> copy(isHomeTasksErrorAnimation = msg.isError)
            is LessonReportStore.Message.IsHomeTasksSavedAnimation -> copy(isHomeTasksSavedAnimation = msg.isSaved)
            is LessonReportStore.Message.HomeTasksToEditIdsUpdated -> copy(homeTasksToEditIds = msg.homeTasksToEditIds)
            is LessonReportStore.Message.TabLoginsIdUpdated -> copy(tabLogins = msg.tabLogins, newTabLogins = msg.tabLogins ?: listOf())
            is LessonReportStore.Message.NewTabsLoginsUpdated -> copy(newTabLogins = msg.logins)
            is LessonReportStore.Message.SaveTabLoginsUpdated -> copy(homeTasksNewTabs = msg.tabs)
        }
    }
}