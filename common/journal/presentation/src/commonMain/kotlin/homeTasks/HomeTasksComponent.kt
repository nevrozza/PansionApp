package homeTasks

import JournalRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.networkInterface.NetworkInterface
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class HomeTasksComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    login: String,
    avatarId: Int,
    name: String,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()

    val nInterfaceName = "HomeTasksInterfaceName"

    val nInterface =
        NetworkInterface(childContext(nInterfaceName + "CONTEXT"), storeFactory, nInterfaceName)
    val nInitInterface =
        NetworkInterface(childContext("INIT" + nInterfaceName + "CONTEXT"), storeFactory,
            "INIT$nInterfaceName"
        )

    val journalRepository: JournalRepository = Inject.instance()

    private val allGroupMarksStore =
        instanceKeeper.getStore {
            HomeTasksStoreFactory(
                storeFactory = storeFactory,
                login = login,
                avatarId = avatarId,
                name = name,
                journalRepository = journalRepository,
                nInitInterface = nInitInterface,
                nInterface = nInterface
            ).create()
        }

    val model = allGroupMarksStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HomeTasksStore.State> = allGroupMarksStore.stateFlow

    fun onEvent(event: HomeTasksStore.Intent) {
        allGroupMarksStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }


    init {
        onEvent(HomeTasksStore.Intent.Init)
    }

    sealed class Output {
        data object Back : Output()
    }
}