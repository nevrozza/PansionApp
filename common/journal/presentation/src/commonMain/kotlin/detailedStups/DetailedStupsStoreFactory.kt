package detailedStups

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import detailedStups.DetailedStupsStore.Intent
import detailedStups.DetailedStupsStore.Label
import detailedStups.DetailedStupsStore.State

class DetailedStupsStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: DetailedStupsExecutor
) {

    fun create(): DetailedStupsStore {
        return DetailedStupsStoreImpl()
    }

    private inner class DetailedStupsStoreImpl :
        DetailedStupsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "DetailedStupsStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = DetailedStupsReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}