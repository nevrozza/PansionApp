package lessonReport

import AuthRepository
import JournalRepository
import ReportData
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import server.Moderation
import server.fetchTitle
import server.getEdYear
import server.getLocalDate

class LessonReportStoreFactory(
    private val storeFactory: StoreFactory,
    private val setMarkMenuComponent: ListComponent,
    private val setDzMarkMenuComponent: ListComponent,
    private val deleteMarkMenuComponent: ListComponent,
    private val setLateTimeMenuComponent: ListComponent,
    private val nInterface: NetworkInterface,
    private val nHomeTasksInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val authRepository: AuthRepository,
    private val data: ReportData,
    private val marksDialogComponent: CAlertDialogComponent
) {



    fun create(): LessonReportStore {
        return LessonReportStoreImpl()
    }

    private inner class LessonReportStoreImpl :
        LessonReportStore,
        Store<LessonReportStore.Intent, LessonReportStore.State, LessonReportStore.Label> by storeFactory.create(
            name = "LessonReportStore",
            initialState = LessonReportStore.State(
                lessonReportId = data.header.reportId,
                isEditable = data.header.teacherLogin == authRepository.fetchLogin()
                             || authRepository.fetchModeration() in listOf(Moderation.both, Moderation.moderator),
                subjectName = data.header.subjectName,
                groupName = data.header.groupName,
                groupId = data.header.groupId,
                teacherName = data.header.teacherName,
                date = data.header.date,
                edYear = getEdYear(getLocalDate(data.header.date)),
                time = data.header.time,
                editTime = data.editTime,
                topic = data.header.theme,
                description = data.description,
                status = data.header.status,
                ids = data.ids,
                isMentorWas = data.isMentorWas,
                columnNames = getColumns(((data.customColumns) - "")),
                subjectId = data.header.subjectId,
                module = data.header.module.toIntOrNull() ?: 1,
                isModer = authRepository.fetchModeration() in listOf(Moderation.both, Moderation.moderator)
            ),
            executorFactory = {
                LessonReportExecutor(
                    setMarkMenuComponent = setMarkMenuComponent,
                    deleteMarkMenuComponent = deleteMarkMenuComponent,
                    setLateTimeMenuComponent = setLateTimeMenuComponent,
                    nInterface = nInterface,
                    journalRepository = journalRepository,
                    marksDialogComponent = marksDialogComponent,
                    authRepository = authRepository,
                    header = data.header,
                    nHomeTasksInterface = nHomeTasksInterface,
                    setDzMarkMenuComponent = setDzMarkMenuComponent
                )
            },
            reducer = LessonReportReducer
        )
}

private fun getColumns(cColumns: List<String>) : List<ReportColumn> {
    val columns = mutableListOf(
        ReportColumn(
            title = prisut,
            type = ColumnTypes.prisut
        ),
        ReportColumn(
            title = srBall,
            type = ColumnTypes.srBall
        ),
        ReportColumn(
            title = opozdanie,
            type = ColumnTypes.opozdanie
        )
    )

    cColumns.forEach {
        val title = (it+ fetchTitle(it)).removePrefix("!").filter { !it.isDigit() }
        columns.add(ReportColumn(title = title, type = it))
    }

    return columns.sortedBy { customOrder[it.type] }
}