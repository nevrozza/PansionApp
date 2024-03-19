package home

import AuthRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class HomeComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
    private val authRepository: AuthRepository = Inject.instance()
    private val homeStore =
        instanceKeeper.getStore {
            HomeStoreFactory(
                storeFactory = storeFactory,
                authRepository = authRepository
            ).create()
        }

    val model = homeStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HomeStore.State> = homeStore.stateFlow

    fun onEvent(event: HomeStore.Intent) {
        homeStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object NavigateToSettings : Output()
    }
}