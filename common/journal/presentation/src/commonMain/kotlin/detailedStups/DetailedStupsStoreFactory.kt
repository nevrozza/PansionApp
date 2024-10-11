package detailedStups

import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import detailedStups.DetailedStupsStore.Intent
import detailedStups.DetailedStupsStore.Label
import detailedStups.DetailedStupsStore.State

class DetailedStupsStoreFactory(
    private val storeFactory: StoreFactory,
    private val login: String,
    private val reason: String,
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository,
    private val name: String,
    private val avatarId: Int,
) {

    fun create(): DetailedStupsStore {
        return DetailedStupsStoreImpl()
    }

    private inner class DetailedStupsStoreImpl :
        DetailedStupsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "DetailedStupsStore",
            initialState = DetailedStupsStore.State(
                login = login,
                reason = reason,
                name = name,
                avatarId = avatarId
            ),
            executorFactory = { DetailedStupsExecutor(
                nInterface = nInterface,
                journalRepository = journalRepository
            ) },
            reducer = DetailedStupsReducer
        )
}