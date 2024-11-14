package allGroupMarks

import AuthRepository
import JournalRepository
import ReportData
import SettingsRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import di.Inject
import homeTasksDialog.HomeTasksDialogComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
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
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()

    private val homeTaskDialogContentName = "homeTaskDialogContentNameAllGroups"
    val homeTasksDialogComponent = HomeTasksDialogComponent(
        componentContext = childContext(homeTaskDialogContentName+"CONTEXT"),
        storeFactory = storeFactory,
        groupId = groupId,
        openReport = { onEvent(AllGroupMarksStore.Intent.OpenFullReport(it)) }
    )

    private val authRepository: AuthRepository = Inject.instance()
    val setingsRepository: SettingsRepository = Inject.instance()

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

    private val allGroupMarksStore =
        instanceKeeper.getStore(key = "AllGroupMarksComponent$groupId") {
            AllGroupMarksStoreFactory(
                storeFactory = storeFactory,
                groupId = groupId,
                groupName = groupName,
                subjectId = subjectId,
                subjectName = subjectName,
                nInterface = nInterface,
                journalRepository = journalRepository,
                stupsDialogComponent = stupsDialogComponent,
                nOpenReportInterface = nOpenReportInterface,
                login = login,
                isModer = authRepository.fetchModeration() in listOf(Moderation.both, Moderation.moderator),
                settingsRepository = Inject.instance()
            ).create()
        }

    val model = allGroupMarksStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<AllGroupMarksStore.State> = allGroupMarksStore.stateFlow

    fun onEvent(event: AllGroupMarksStore.Intent) {
        allGroupMarksStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    init {
        onEvent(AllGroupMarksStore.Intent.Init)
    }

    sealed class Output {
        data object Back : Output()
        data class OpenReport(val reportData: ReportData) : Output()
    }
}