package studentReportDialog

import JournalRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class StudentReportComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()

    val nInterfaceName = "StudentReportDialogInterfaceName"

//    val nInterface =
//        NetworkInterface(childContext(nInterfaceName + "CONTEXT"), storeFactory, nInterfaceName)


    val dialog = CBottomSheetComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = nInterfaceName
    )

    val journalRepository: JournalRepository = Inject.instance()

    private val studentReportDialogStore =
        instanceKeeper.getStore {
            StudentReportDialogStoreFactory(
                storeFactory = storeFactory,
                journalRepository = journalRepository,
                dialog = dialog
            ).create()
        }

    val model = studentReportDialogStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<StudentReportDialogStore.State> = studentReportDialogStore.stateFlow

    fun onEvent(event: StudentReportDialogStore.Intent) {
        studentReportDialogStore.accept(event)
    }
}