package homeTasks

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import homeTasks.HomeTasksStore.Intent
import homeTasks.HomeTasksStore.Label
import homeTasks.HomeTasksStore.State
import homeTasks.HomeTasksStore.Message

class HomeTasksStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): HomeTasksStore {
        return HomeTasksStoreImpl()
    }

    private inner class HomeTasksStoreImpl :
        HomeTasksStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeTasksStore",
            initialState = HomeTasksStore.State,
            executorFactory = { HomeTasksExecutor() },
            reducer = HomeTasksReducer
        )
}