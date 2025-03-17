package journal

import AuthRepository
import ReportData
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import di.Inject
import journal.init.TeacherGroup
import report.ReportHeader
import server.getDate
import server.getSixTime

//data class JournalComponentData(
//    val header: ReportHeader
//)

class JournalComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<JournalStore.Intent, JournalStore.State, JournalStore.Label> {
    private val authRepository: AuthRepository = Inject.instance()

    val nInterface = NetworkInterface(componentContext, storeFactory, "journalCComponent")
    val nOpenReportInterface = NetworkInterface(componentContext, storeFactory, "nOpenReportComponent")
    val groupListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "groupListInMainJournal",
        onItemClick = {
            onEvent(
                JournalStore.Intent.OnGroupClicked(
                    it.id.toInt(), getSixTime(),
                    date = null, lessonId = null
                )
            )
        })

    val fGroupListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "filterGroupListInMainJournal",
        onItemClick = {
            onEvent(JournalStore.Intent.FilterGroup(it.id.toInt()))
        })
    val fDateListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "filterDateListInMainJournal",
        onItemClick = {
            onEvent(JournalStore.Intent.FilterDate(it.id))
        })
    val fTeachersListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "filterTeacherListInMainJournal",
        onItemClick = {
            onEvent(JournalStore.Intent.FilterTeacher(it.id))
        })
    val fStatusListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "filterStatusListInMainJournal",
        onItemClick = {
            onEvent(JournalStore.Intent.FilterStatus(it.id.toBoolean()))
        })


    val studentsInGroupCAlertDialogComponent = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "studentsInGroupListCAlertDialogInMainJournal",
        onAcceptClick = {
            createReport()
        },
        onDeclineClick = {
            //some magic..
            groupListComponent.onEvent(ListDialogStore.Intent.ShowDialog)
            hideStudentAlarm()
        }
    )

    private fun hideStudentAlarm() {
        studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
    }

    private fun getReportHeader(
        teacherGroups: List<TeacherGroup> = store.state.teacherGroups
    ): ReportHeader {
        val group =
            teacherGroups.first { it.cutedGroup.groupId == store.state.currentGroupId }
        val time = if (store.state.time != "") store.state.time else getSixTime()
        onEvent(JournalStore.Intent.ResetTime)
        return ReportHeader(
            reportId = model.value.creatingReportId,
            subjectName = group.subjectName,
            subjectId = group.subjectId, //не нужное
            groupName = group.cutedGroup.groupName,
            groupId = store.state.currentGroupId,
            teacherName = "${authRepository.fetchSurname()} ${authRepository.fetchName()[0]}. ${authRepository.fetchPraname()[0]}.",
            teacherLogin = authRepository.fetchLogin(),
            date = getDate(),
            time = time,
            status = false,
            module = store.state.currentModule,
            theme = ""
        )
    }

    fun createReport(header: ReportHeader = getReportHeader()) {
        hideStudentAlarm()

        val data = ReportData(
            header = header,
//            topic = "",
            description = "",
            editTime = "",
            ids = 0,
            isMentorWas = false,
            isEditable = true,
            customColumns = emptyList()
        )
        openReport(data)
    }

    fun openReport(reportData: ReportData) {
        onOutput(
            Output.NavigateToLessonReport(
                reportData
            )
        )
    }

    override val store =
        instanceKeeper.getStore {
            JournalStoreFactory(
                storeFactory = storeFactory,
                groupListComponent = groupListComponent,
                studentsInGroupCAlertDialogComponent = studentsInGroupCAlertDialogComponent,
                nInterface = nInterface,
                nOpenReportInterface = nOpenReportInterface,
                fTeachersListComponent = fTeachersListComponent,
                fGroupListComponent = fGroupListComponent,
                fDateListComponent = fDateListComponent,
                fStatusListComponent = fStatusListComponent,
                authRepository = authRepository
            ).create()
        }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object NavigateToSettings : Output()
        data class NavigateToLessonReport(val reportData: ReportData) : Output()
    }
}