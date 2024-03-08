package groups

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.listDialog.ListDialogComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class GroupsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
    private val adminRepository: AdminRepository = Inject.instance()
    val formListDialogComponent = ListDialogComponent(
        componentContext,
        storeFactory,
        name = "formListInGroups",
        onItemClick = {
            onEvent(GroupsStore.Intent.CreateUserForm(it.id))
        })

    private val groupsStore =
        instanceKeeper.getStore {
            GroupsStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                formListDialogComponent
//                authRepository = authRepository
            ).create()
        }

    val model = groupsStore.asValue()

    private val backCallback = BackCallback {
        onOutput(Output.BackToAdmin)
    }


    init {
        backHandler.register(backCallback)
        onEvent(GroupsStore.Intent.InitList)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<GroupsStore.State> = groupsStore.stateFlow

    fun onEvent(event: GroupsStore.Intent) {
        groupsStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object BackToAdmin : Output()
    }
}