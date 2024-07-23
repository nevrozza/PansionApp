package allGroupMarks

import AuthRepository
import JournalRepository
import ReportData
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import detailedStups.DetailedStupsStore
import detailedStups.DetailedStupsStoreFactory
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class AllGroupMarksComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    private val groupId: Int,
    private val groupName: String,
    private val subjectId: Int,
    private val subjectName: String,
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
    private val authRepository: AuthRepository = Inject.instance()

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
                login = authRepository.fetchLogin()
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

    private val backCallback = BackCallback {
        onOutput(Output.BackToHome)
    }


    init {
        backHandler.register(backCallback)
        onEvent(AllGroupMarksStore.Intent.Init)
    }

    sealed class Output {
        data object BackToHome : Output()
        data class OpenReport(val reportData: ReportData) : Output()
    }
}