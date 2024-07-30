package cabinets

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
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


    private val cabinetsStore =
        instanceKeeper.getStore {
            CabinetsStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                nInterface = nInterface
            ).create()
        }


    init {
        onEvent(CabinetsStore.Intent.Init)
    }

    val model = cabinetsStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: CabinetsStore.Intent) {
        cabinetsStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}