package ministry

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ministry.MinistryStore.Intent
import ministry.MinistryStore.Label
import ministry.MinistryStore.State

class MinistryStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: MinistryExecutor
) {

    fun create(): MinistryStore {
        return MinistryStoreImpl()
    }

    private inner class MinistryStoreImpl :
        MinistryStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "MinistryStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = MinistryReducer
        )
}