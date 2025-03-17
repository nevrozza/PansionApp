package cabinets

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import decompose.getChildContext

class CabinetsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<CabinetsStore.Intent, CabinetsStore.State, CabinetsStore.Label> {
    private val cabinetsComponentNInterfaceName = "cabinetsComponentNInterface"
    val nInterface = NetworkInterface(
        getChildContext(cabinetsComponentNInterfaceName),
        storeFactory,
        cabinetsComponentNInterfaceName
    )


    override val store =
        instanceKeeper.getStore {
            CabinetsStoreFactory(
                storeFactory = storeFactory,
                executor = CabinetsExecutor(
                    nInterface = nInterface
                )
            ).create()
        }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}