package cabinets

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.mpChose.mpChoseComponent
import components.networkInterface.NetworkInterface
import di.Inject

class CabinetsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "cabinetsComponentNInterface"
    )

    private val adminRepository: AdminRepository = Inject.instance()


    private val scheduleStore =
        instanceKeeper.getStore {
            CabinetsStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                nInterface = nInterface
            ).create()
        }

    private val backCallback = BackCallback {
        onOutput(Output.BackToAdmin)
    }


    init {
        backHandler.register(backCallback)
        onEvent(CabinetsStore.Intent.Init)
    }

    val model = scheduleStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: CabinetsStore.Intent) {
        scheduleStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object BackToAdmin : Output()
    }
}