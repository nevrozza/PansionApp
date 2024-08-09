package homeTasksDialog

import JournalRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import di.Inject
import homeTasks.HomeTasksStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class HomeTasksDialogComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val groupId: Int,
    val openReport: ((Int) -> Unit)? = null
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()

    private val nInterfaceName = "HomeTasksDialogInterfaceName"
    private val nDialogInterfaceName = "HomeTasksDialogDialogInterfaceName"

    val dialogComponent = CAlertDialogComponent(
        componentContext = childContext(nDialogInterfaceName + "CONTEXT"),
        storeFactory = storeFactory,
        name = nDialogInterfaceName,
        onAcceptClick = {}
    )

    val nInterface =
        NetworkInterface(childContext(nInterfaceName + "CONTEXT"), storeFactory, nInterfaceName)

    val journalRepository: JournalRepository = Inject.instance()

    private val homeTasksDialogStore =
        instanceKeeper.getStore {
            HomeTasksDialogStoreFactory(
                storeFactory = storeFactory,
                journalRepository = journalRepository,
                groupId = groupId,
                nInterface = nInterface
            ).create()
        }

    val model = homeTasksDialogStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<HomeTasksStore.State> = homeTasksDialogStore.stateFlow

    fun onEvent(event: HomeTasksDialogStore.Intent) {
        homeTasksDialogStore.accept(event)
    }

    init {
        //onEvent(HomeTasksDialogStore.Intent.Init)
    }
}