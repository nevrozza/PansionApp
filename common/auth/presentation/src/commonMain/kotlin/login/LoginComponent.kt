package login

import AuthRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class LoginComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val login: String,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    private val authRepository: AuthRepository = Inject.instance()
    private val loginStore =
        instanceKeeper.getStore {
            LoginStoreFactory(
                storeFactory = storeFactory,
                authRepository = authRepository,
                login = login
            ).create()
        }

    val model = loginStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<LoginStore.State> = loginStore.stateFlow

    fun onEvent(event: LoginStore.Intent) {
        loginStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object NavigateToMain : Output()
        data object BackToActivation : Output()
    }
}
