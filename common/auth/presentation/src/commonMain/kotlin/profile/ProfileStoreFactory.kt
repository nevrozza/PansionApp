package profile

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import profile.ProfileStore.Intent
import profile.ProfileStore.Label
import profile.ProfileStore.State

class ProfileStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: ProfileExecutor
) {

    fun create(): ProfileStore {
        return ProfileStoreImpl()
    }

    private inner class ProfileStoreImpl :
        ProfileStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "ProfileStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = ProfileReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}