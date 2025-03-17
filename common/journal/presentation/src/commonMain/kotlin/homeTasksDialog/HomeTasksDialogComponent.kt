package homeTasksDialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent

class HomeTasksDialogComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val groupId: Int,
    val openReport: ((Int) -> Unit)? = null
) : ComponentContext by componentContext, DefaultMVIComponent<HomeTasksDialogStore.Intent, HomeTasksDialogStore.State, HomeTasksDialogStore.Label> {

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

    override val store =
        instanceKeeper.getStore {
            HomeTasksDialogStoreFactory(
                storeFactory = storeFactory,
                executor = HomeTasksDialogExecutor(
                    nInterface = nInterface
                ),
                state = HomeTasksDialogStore.State(groupId = groupId)
            ).create()
        }
}