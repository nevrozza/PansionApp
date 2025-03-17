package homeTasks

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent

class HomeTasksComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    login: String,
    avatarId: Int,
    name: String,
    val updateHTCount: (Int) -> Unit,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<HomeTasksStore.Intent, HomeTasksStore.State, HomeTasksStore.Label> {

    val nInterfaceName = "HomeTasksInterfaceName"

    val nInterface =
        NetworkInterface(childContext(nInterfaceName + "CONTEXT"), storeFactory, nInterfaceName)
    val nInitInterface =
        NetworkInterface(childContext("INIT" + nInterfaceName + "CONTEXT"), storeFactory,
            "INIT$nInterfaceName"
        )
    override val store =
        instanceKeeper.getStore {
            HomeTasksStoreFactory(
                storeFactory = storeFactory,
                executor = HomeTasksExecutor(
                    nInitInterface = nInitInterface,
                    nInterface = nInterface,
                    updateHTCount = { updateHTCount(it) }
                ),
                state = HomeTasksStore.State(
                    login = login,
                    avatarId = avatarId,
                    name = name
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