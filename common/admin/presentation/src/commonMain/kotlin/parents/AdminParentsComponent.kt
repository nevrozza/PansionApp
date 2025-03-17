package parents

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import decompose.getChildContext

class AdminParentsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<AdminParentsStore.Intent, AdminParentsStore.State, AdminParentsStore.Label> {
    private val nInterfaceName = "AdminParentsComponentNInterface"
    val nInterface = NetworkInterface(
        getChildContext(nInterfaceName),
        storeFactory,
        nInterfaceName
    )

    val parentEditPicker = ListComponent(
        componentContext = getChildContext("ParentEditPicker"),
        storeFactory = storeFactory,
        name = "ParentEditPicker",
        onItemClick = {
            onParentEditPickerItemClick(it.id)
        }
    )

    val childCreatePicker = ListComponent(
        componentContext = getChildContext("ChildCreatePicker"),
        storeFactory = storeFactory,
        name = "ChildCreatePicker",
        onItemClick = {
            onChildCreatePickerItemClick(it.id)
        }
    )

    private fun onParentEditPickerItemClick(login: String) {
        onEvent(AdminParentsStore.Intent.PickParent(login))
    }

    private fun onChildCreatePickerItemClick(login: String) {
        onEvent(AdminParentsStore.Intent.CreateChild(login))
    }

    override val store =
        instanceKeeper.getStore {
            AdminParentsStoreFactory(
                storeFactory = storeFactory,
                executor = AdminParentsExecutor(
                    nInterface = nInterface,
                    parentEditPicker = parentEditPicker,
                    childCreatePicker = childCreatePicker
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