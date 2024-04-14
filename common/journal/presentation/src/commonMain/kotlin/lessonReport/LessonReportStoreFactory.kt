package lessonReport

import JournalRepository
import ReportData
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import server.fetchTitle

class LessonReportStoreFactory(
    private val storeFactory: StoreFactory,
    private val setMarkMenuComponent: ListComponent,
    private val deleteMarkMenuComponent: ListComponent,
    private val setLateTimeMenuComponent: ListComponent,
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val data: ReportData,
    private val marksDialogComponent: CAlertDialogComponent,
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
                isEditable = true,
                subjectName = data.header.subjectName,
                groupName = data.header.groupName,
                groupId = data.header.groupId,
                teacherName = data.header.teacherName,
                date = data.header.date,
                time = data.header.time,
                editTime = data.editTime,
                topic = data.topic,
                description = data.description,
                status = data.header.status,
                ids = data.ids,
                isMentorWas = data.isMentorWas,
                columnNames = getColumns(((data.customColumns) - "")),
                subjectId = data.header.subjectId
            ),
            executorFactory = {
                LessonReportExecutor(
                    setMarkMenuComponent = setMarkMenuComponent,
                    deleteMarkMenuComponent = deleteMarkMenuComponent,
                    setLateTimeMenuComponent = setLateTimeMenuComponent,
                    nInterface = nInterface,
                    journalRepository = journalRepository,
                    marksDialogComponent = marksDialogComponent
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