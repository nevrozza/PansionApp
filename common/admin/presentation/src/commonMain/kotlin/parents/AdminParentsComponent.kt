package parents

import AdminRepository
import asValue
import cabinets.CabinetsStore
import cabinets.CabinetsStoreFactory
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import di.Inject

class AdminParentsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    private val nInterfaceName = "AdminParentsComponentNInterface"
    val nInterface = NetworkInterface(
        childContext(nInterfaceName + "CONTEXT"),
        storeFactory,
        nInterfaceName
    )

    private val adminRepository: AdminRepository = Inject.instance()


    private val adminParentsStore =
        instanceKeeper.getStore {
            AdminParentsStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                nInterface = nInterface
            ).create()
        }



    init {
        onEvent(AdminParentsStore.Intent.Init)
    }

    val model = adminParentsStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: AdminParentsStore.Intent) {
        adminParentsStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}