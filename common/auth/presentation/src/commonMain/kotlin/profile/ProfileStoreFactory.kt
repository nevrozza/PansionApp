package profile

import AuthRepository
import FIO
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import profile.ProfileStore.Intent
import profile.ProfileStore.Label
import profile.ProfileStore.State

class ProfileStoreFactory(
    private val storeFactory: StoreFactory,
    private val authRepository: AuthRepository,
    private val fio: FIO
) {

    fun create(): ProfileStore {
        return ProfileStoreImpl()
    }

    private inner class ProfileStoreImpl :
        ProfileStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "ProfileStore",
            initialState = State(
                fio = fio
            ),
            executorFactory = { ProfileExecutor(
                authRepository = authRepository
            ) },
            reducer = ProfileReducer
        )
}