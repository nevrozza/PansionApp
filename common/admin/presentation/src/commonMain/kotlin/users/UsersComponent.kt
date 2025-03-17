package users

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import decompose.getChildContext

class UsersComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<UsersStore.Intent, UsersStore.State, UsersStore.Label> {


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
        getChildContext(eDeleteDialogName),
        storeFactory = storeFactory,
        name = eDeleteDialogName,
        onAcceptClick = { onEvent(UsersStore.Intent.DeleteAccount) },
        onDeclineClick = { onEvent(UsersStore.Intent.DeleteAccountInit(null)) }
    )

    override val store =
        instanceKeeper.getOrCreate {
            UsersStoreFactory(
                storeFactory = storeFactory,
                UsersExecutor(
                    nUsersInterface = nUsersInterface,
                    eUserBottomSheet = eUserBottomSheet,
                    cUserBottomSheet = cUserBottomSheet,
                    eDeleteDialog = eDeleteDialog
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