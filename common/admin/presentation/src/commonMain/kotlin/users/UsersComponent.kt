package users

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import components.cBottomSheet.CBottomSheetComponent
import di.Inject
import users.UsersStore.State

class UsersComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    private val nUsersInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "usersComponentNInterface"
    )
    val nModel = nUsersInterface.networkModel

    private val adminRepository: AdminRepository = Inject.instance()

    val cUserBottomSheet = CBottomSheetComponent(
        componentContext,
        storeFactory,
        name = "creatingUserBottomSheet"
    )

    val eUserBottomSheet = CBottomSheetComponent(
        componentContext,
        storeFactory,
        name = "editingUserBottomSheet"
    )

    private val usersStore =
        instanceKeeper.getStore(key = "UsersStoreState") {
            println("Store: $stateKeeper")
            println("Instance: $instanceKeeper")
            UsersStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                nUsersInterface = nUsersInterface,
                eUserBottomSheet = eUserBottomSheet,
                cUserBottomSheet = cUserBottomSheet
            ).create(stateKeeper)
        }

    private val backCallback = BackCallback {
        onOutput(Output.BackToAdmin)
    }


    init {
        println("INITED: sad")

        backHandler.register(backCallback)
//        if(!stKeeper.isRegistered("UsersStoreState")) {

//        }
        onEvent(UsersStore.Intent.FetchUsersInit)
    }

    val model = usersStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: UsersStore.Intent) {
        usersStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object BackToAdmin : Output()
    }
}