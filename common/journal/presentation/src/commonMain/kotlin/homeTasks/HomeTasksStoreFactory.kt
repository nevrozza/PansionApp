package homeTasks

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import homeTasks.HomeTasksStore.Intent
import homeTasks.HomeTasksStore.Label
import homeTasks.HomeTasksStore.State

class HomeTasksStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: HomeTasksExecutor
) {

    fun create(): HomeTasksStore {
        return HomeTasksStoreImpl()
    }

    private inner class HomeTasksStoreImpl :
        HomeTasksStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeTasksStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = HomeTasksReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}