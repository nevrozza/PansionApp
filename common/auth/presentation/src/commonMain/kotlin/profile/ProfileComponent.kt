package profile

import AuthRepository
import FIO
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.networkInterface.NetworkInterface
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import login.LoginStore

class ProfileComponent(componentContext: ComponentContext,
                       storeFactory: StoreFactory,
                       fio: FIO,
                       private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    private val authRepository: AuthRepository = Inject.instance()
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "profileStoreNetworkInterface"
    )
    private val profileStore =
        instanceKeeper.getStore {
            ProfileStoreFactory(
                storeFactory = storeFactory,
                authRepository = authRepository,
                fio = fio
            ).create()
        }

    val model = profileStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<ProfileStore.State> = profileStore.stateFlow

    fun onEvent(event: ProfileStore.Intent) {
        profileStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object BackToHome : Output()
//        data object BackToActivation : Output()
    }
}