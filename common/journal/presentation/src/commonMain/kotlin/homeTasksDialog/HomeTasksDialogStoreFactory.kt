package homeTasksDialog

import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import homeTasksDialog.HomeTasksDialogStore.Intent
import homeTasksDialog.HomeTasksDialogStore.Label
import homeTasksDialog.HomeTasksDialogStore.State
import homeTasksDialog.HomeTasksDialogStore.Message

class HomeTasksDialogStoreFactory(
    private val storeFactory: StoreFactory,
    private val journalRepository: JournalRepository,
    private val nInterface: NetworkInterface,
    private val groupId: Int
) {

    fun create(): HomeTasksDialogStore {
        return HomeTasksDialogStoreImpl()
    }

    private inner class HomeTasksDialogStoreImpl :
        HomeTasksDialogStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeTasksDialogStore",
            initialState = HomeTasksDialogStore.State(groupId = groupId),
            executorFactory = { HomeTasksDialogExecutor(
                journalRepository = journalRepository,
                nInterface = nInterface
            ) },
            reducer = HomeTasksDialogReducer
        )
}