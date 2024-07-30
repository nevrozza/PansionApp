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

class ProfileComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    studentLogin: String,
    fio: FIO,
    avatarId: Int,
    val changeAvatarOnMain: (Int) -> Unit,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    private val authRepository: AuthRepository = Inject.instance()
    val nAboutMeInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "profileStoreNetworkInterface"
    )
    val nAvatarInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "profileAvatarStoreNetworkInterface"
    )
    private val profileStore =
        instanceKeeper.getStore {
            ProfileStoreFactory(
                storeFactory = storeFactory,
                authRepository = authRepository,
                fio = fio,
                studentLogin = studentLogin,
                avatarId = avatarId,
                nAvatarInterface = nAvatarInterface,
                nAboutMeInterface = nAboutMeInterface,
                changeAvatarOnMain = { changeAvatarOnMain(it) }
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

    init {
        onEvent(ProfileStore.Intent.Init)
    }

    sealed class Output {
        data object Back : Output()
//        data object BackToActivation : Output()
    }
}