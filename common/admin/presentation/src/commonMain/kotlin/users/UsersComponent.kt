package users

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import di.Inject
import users.UsersStore.State

class UsersComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {


    private val nUsersInterface = NetworkInterface(
        childContext("usersComponentNInterfaceContext"),
        storeFactory,
        "usersComponentNInterface"
    )
    val nModel = nUsersInterface.networkModel


    val cUserBottomSheet = CBottomSheetComponent(
        childContext("creatingUserBottomSheetContext"),
        storeFactory,
        name = "creatingUserBottomSheet"
    )

    val eUserBottomSheet = CBottomSheetComponent(
        childContext("editingUserBottomSheetContext"),
        storeFactory,
        name = "editingUserBottomSheet"
    )

    private val eDeleteDialogName = "EDeleteDialogNameUserComponent"

    val eDeleteDialog = CAlertDialogComponent(
        childContext(eDeleteDialogName + "CONTEXT"),
        storeFactory = storeFactory,
        name = eDeleteDialogName,
        onAcceptClick = { onEvent(UsersStore.Intent.DeleteAccount) },
        onDeclineClick = { onEvent(UsersStore.Intent.DeleteAccountInit(null)) }
    )

    private val adminRepository: AdminRepository = Inject.instance()
    private val usersStore =
        instanceKeeper.getOrCreate {

            UsersStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                nUsersInterface = nUsersInterface,
                eUserBottomSheet = eUserBottomSheet,
                cUserBottomSheet = cUserBottomSheet,
                eDeleteDialog = eDeleteDialog
            ).create(
                stateKeeper.consume(
                    key = "USERS_STORE_STATE",
                    strategy = State.serializer()
                ),
                stateKeeper
            )
        }

    val model = usersStore.asValue()

    init {
        //stateKeeper.register("USERS_STORE_STATE", strategy = State.serializer(), supplier = usersStore::state)


//        if(!stKeeper.isRegistered("UsersStoreState")) {

//        }
        onEvent(UsersStore.Intent.FetchUsersInit)
    }


//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: UsersStore.Intent) {
        usersStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object Back : Output()
    }
}