package admin

import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class AdminComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()
    private val adminStore =
        instanceKeeper.getStore {
            AdminStoreFactory(
                storeFactory = storeFactory,
//                authRepository = authRepository
            ).create()
        }

    val model = adminStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<AdminStore.State> = adminStore.stateFlow

    fun onEvent(event: AdminStore.Intent) {
        adminStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
//        data object NavigateToMentors : Output()
        data object NavigateToUsers : Output()
        data object NavigateToGroups : Output()
//        data object NavigateToStudents : Output()

    }
}

data class AdminItem(
    val title: String,
    val routing: AdminComponent.Output
)