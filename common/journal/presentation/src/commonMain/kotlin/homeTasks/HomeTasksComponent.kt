package homeTasks

import JournalRepository
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

class HomeTasksComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()

    val nInterface =
        NetworkInterface(componentContext, storeFactory, "HomeTasksComponent")

    val journalRepository: JournalRepository = Inject.instance()

    private val allGroupMarksStore =
        instanceKeeper.getStore {
            HomeTasksStoreFactory(
                storeFactory = storeFactory
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

    private val backCallback = BackCallback {
        onOutput(Output.BackToHome)
    }


    init {
        backHandler.register(backCallback)
//        onEvent(HomeTasksStore.Intent.Init)
    }

    sealed class Output {
        data object BackToHome : Output()
    }
}