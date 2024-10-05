package profile

import AuthRepository
import FIO
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import profile.ProfileStore.Intent
import profile.ProfileStore.Label
import profile.ProfileStore.State

class ProfileStoreFactory(
    private val storeFactory: StoreFactory,
    private val authRepository: AuthRepository,
    private val studentLogin: String,
    private val fio: FIO,
    private val avatarId: Int,
    private val isOwner: Boolean,
    private val isCanEdit: Boolean,
    private val nAvatarInterface: NetworkInterface,
    private val nAboutMeInterface: NetworkInterface,
    private val changeAvatarOnMain: (Int) -> Unit
) {

    fun create(): ProfileStore {
        return ProfileStoreImpl()
    }

    private inner class ProfileStoreImpl :
        ProfileStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "ProfileStore",
            initialState = State(
                studentLogin = studentLogin,
                fio = fio,
                avatarId = avatarId,
                isOwner = isOwner,
                isCanEdit = isCanEdit
            ),
            executorFactory = { ProfileExecutor(
                authRepository = authRepository,
                nAvatarInterface = nAvatarInterface,
                changeAvatarOnMain = { changeAvatarOnMain(it) },
                nAboutMeInterface = nAboutMeInterface
            ) },
            reducer = ProfileReducer
        )
}