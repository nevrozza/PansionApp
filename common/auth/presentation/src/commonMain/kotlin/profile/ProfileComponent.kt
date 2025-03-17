package profile

import FIO
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent

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
) : ComponentContext by componentContext, DefaultMVIComponent<ProfileStore.Intent, ProfileStore.State, ProfileStore.Label> {
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


    override val store =
        instanceKeeper.getStore {
            ProfileStoreFactory(
                storeFactory = storeFactory,
                state = ProfileStore.State(
                    studentLogin = studentLogin,
                    fio = fio,
                    avatarId = avatarId,
                    isOwner = isOwner,
                    isCanEdit = isCanEdit
                ),
                executor = ProfileExecutor(
                    nAvatarInterface = nAvatarInterface,
                    changeAvatarOnMain = { changeAvatarOnMain(it) },
                    nAboutMeInterface = nAboutMeInterface
                )

            ).create()
        }


    fun onOutput(output: Output) {
        output(output)
    }


    sealed class Output {
        data object Back : Output()
        data class OpenAchievements(val login: String, val name: String, val avatarId: Int) :
            Output()
    }
}