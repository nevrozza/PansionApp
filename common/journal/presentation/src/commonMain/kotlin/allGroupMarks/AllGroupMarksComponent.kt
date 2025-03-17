package allGroupMarks

import AuthRepository
import JournalRepository
import ReportData
import SettingsRepository
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import di.Inject
import homeTasksDialog.HomeTasksDialogComponent
import server.Moderation

class AllGroupMarksComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    private val groupId: Int,
    private val groupName: String,
    private val subjectId: Int,
    private val subjectName: String,
    private val login: String
) : ComponentContext by componentContext, DefaultMVIComponent<AllGroupMarksStore.Intent, AllGroupMarksStore.State, AllGroupMarksStore.Label> {

    private val homeTaskDialogContentName = "homeTaskDialogContentNameAllGroups"
    val homeTasksDialogComponent = HomeTasksDialogComponent(
        componentContext = childContext(homeTaskDialogContentName+"CONTEXT"),
        storeFactory = storeFactory,
        groupId = groupId,
        openReport = { onEvent(AllGroupMarksStore.Intent.OpenFullReport(it)) }
    )

    private val authRepository: AuthRepository = Inject.instance()
    val settingsRepository: SettingsRepository = Inject.instance()

    val nInterface =
        NetworkInterface(componentContext, storeFactory, "AllGroupMarksComponent")
    val nOpenReportInterface =
        NetworkInterface(componentContext, storeFactory, "OpenReportAllGroupMarksComponent")

    val journalRepository: JournalRepository = Inject.instance()

    val stupsDialogComponent = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "StupsDialogComponentIntAllGroupMarks",
        {}
    )

    override val store =
        instanceKeeper.getStore(key = "AllGroupMarksComponent$groupId") {
            AllGroupMarksStoreFactory(
                storeFactory = storeFactory,
                executor = AllGroupMarksExecutor(
                    nInterface = nInterface,
                    stupsDialogComponent = stupsDialogComponent,
                    nOpenReportInterface = nOpenReportInterface
                ),
                state = AllGroupMarksStore.State(
                    groupId = groupId,
                    groupName = groupName,
                    subjectId = subjectId,
                    subjectName = subjectName,
                    login = login,
                    isModer = authRepository.fetchModeration() in listOf(Moderation.BOTH, Moderation.MODERATOR),
                    isTableView = settingsRepository.fetchIsMarkTable()
                )
            ).create()
        }

    fun onOutput(output: Output) {
        output(output)
    }


    sealed class Output {
        data object Back : Output()
        data class OpenReport(val reportData: ReportData) : Output()
    }
}