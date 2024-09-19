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
import components.listDialog.ListComponent
import components.listDialog.ListDialogExecutor
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


    val parentEditPicker = ListComponent(
        componentContext = childContext("ParentEditPicker" + "CONTEXT"),
        storeFactory = storeFactory,
        name = "ParentEditPicker",
        onItemClick = {
            onParentEditPickerItemClick(it.id)
        }
    )

    val childCreatePicker = ListComponent(
        componentContext = childContext("ChildCreatePicker" + "CONTEXT"),
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

    private val adminParentsStore =
        instanceKeeper.getStore {
            AdminParentsStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                nInterface = nInterface,
                parentEditPicker = parentEditPicker,
                childCreatePicker = childCreatePicker
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