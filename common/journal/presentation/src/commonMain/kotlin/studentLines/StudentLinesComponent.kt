package studentLines

import JournalRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.networkInterface.NetworkInterface
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import studentReportDialog.StudentReportComponent

class StudentLinesComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    login: String,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()

    val nInterfaceName = "StudentLinesInterfaceName"

    val studentReportDialog = StudentReportComponent(
        componentContext = childContext(nInterfaceName + "DIALOGCONTEXT"),
        storeFactory = storeFactory
    )

    val nInterface =
        NetworkInterface(childContext(nInterfaceName + "CONTEXT"), storeFactory, nInterfaceName)

    val journalRepository: JournalRepository = Inject.instance()

    private val studentLinesStore =
        instanceKeeper.getStore {
            StudentLinesStoreFactory(
                storeFactory = storeFactory,
                login = login,
                journalRepository = journalRepository,
                nInterface = nInterface
            ).create()
        }

    val model = studentLinesStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<StudentLinesStore.State> = studentLinesStore.stateFlow

    fun onEvent(event: StudentLinesStore.Intent) {
        studentLinesStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }


    init {
        onEvent(StudentLinesStore.Intent.Init)
    }

    sealed class Output {
        data object Back : Output()
    }
}