package users

import AdminRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import mentors.MentorsStore
import mentors.MentorsStoreFactory

class UsersComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
    private val adminRepository: AdminRepository = Inject.instance()
    private val usersStore =
        instanceKeeper.getStore {
            UsersStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository
//                authRepository = authRepository
            ).create()
        }

    private val backCallback = BackCallback {
        onOutput(Output.BackToAdmin)
    }


    init {
        backHandler.register(backCallback)
        onEvent(UsersStore.Intent.FetchUsersInit)
    }

    val model = usersStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

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