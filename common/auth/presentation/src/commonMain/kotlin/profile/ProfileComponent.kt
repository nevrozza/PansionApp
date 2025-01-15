package profile

import AuthRepository
import FIO
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cBottomSheet.CBottomSheetComponent
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
    isOwner: Boolean,
    isCanEdit: Boolean,
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

    val giaCBottomSheetComponent = CBottomSheetComponent(
        componentContext = childContext("giaCBottomSheetComponentCONTEXT"),
        storeFactory = storeFactory,
        name = "giaCBottomSheetComponent"
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
                changeAvatarOnMain = { changeAvatarOnMain(it) },
                isOwner = isOwner,
                isCanEdit = isCanEdit
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
        data class OpenAchievements(val login: String, val name: String, val avatarId: Int) : Output()
//        data object BackToActivation : Output()
    }
}