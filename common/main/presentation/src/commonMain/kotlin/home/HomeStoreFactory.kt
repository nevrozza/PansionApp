package home

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import home.HomeStore.Intent
import home.HomeStore.Label
import home.HomeStore.State

class HomeStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: HomeExecutor
) {

    fun create(): HomeStore {
        return HomeStoreImpl()
    }

    private inner class HomeStoreImpl :
        HomeStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "HomeStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = HomeReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}