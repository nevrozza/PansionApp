package ministry

import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import ministry.MinistryStore.Intent
import ministry.MinistryStore.Label
import ministry.MinistryStore.State

class MinistryStoreFactory(
    private val storeFactory: StoreFactory,
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository
) {

    fun create(): MinistryStore {
        return MinistryStoreImpl()
    }

    private inner class MinistryStoreImpl :
        MinistryStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "MinistryStore",
            initialState = MinistryStore.State(),
            executorFactory = { MinistryExecutor(
                nInterface = nInterface,
                journalRepository = journalRepository
            ) },
            reducer = MinistryReducer
        )
}