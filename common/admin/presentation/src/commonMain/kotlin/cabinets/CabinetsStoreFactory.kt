package cabinets

import cabinets.CabinetsStore.Intent
import cabinets.CabinetsStore.Label
import cabinets.CabinetsStore.State
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class CabinetsStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: CabinetsExecutor
) {

    fun create(): CabinetsStore {
        return CabinetsStoreImpl()
    }

    private inner class CabinetsStoreImpl :
        CabinetsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "CabinetsStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = CabinetsReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}