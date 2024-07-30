package homeTasks

import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import homeTasks.HomeTasksStore.Intent
import homeTasks.HomeTasksStore.Label
import homeTasks.HomeTasksStore.State
import homeTasks.HomeTasksStore.Message

class HomeTasksStoreFactory(
    private val storeFactory: StoreFactory,
    private val login: String,
    private val avatarId: Int,
    private val name: String,
    private val journalRepository: JournalRepository,
    private val nInitInterface: NetworkInterface,
    private val nInterface: NetworkInterface
) {

    fun create(): HomeTasksStore {
        return HomeTasksStoreImpl()
    }

    private inner class HomeTasksStoreImpl :
        HomeTasksStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeTasksStore",
            initialState = HomeTasksStore.State(
                login = login,
                avatarId = avatarId,
                name = name
            ),
            executorFactory = { HomeTasksExecutor(
                journalRepository = journalRepository,
                nInitInterface = nInitInterface,
                nInterface = nInterface
            ) },
            reducer = HomeTasksReducer
        )
}