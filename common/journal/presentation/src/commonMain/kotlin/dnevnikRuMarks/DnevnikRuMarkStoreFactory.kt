package dnevnikRuMarks

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import dnevnikRuMarks.DnevnikRuMarkStore.Intent
import dnevnikRuMarks.DnevnikRuMarkStore.Label
import dnevnikRuMarks.DnevnikRuMarkStore.State

class DnevnikRuMarkStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: DnevnikRuMarkExecutor
) {

    fun create(): DnevnikRuMarkStore {
        return DnevnikRuMarkStoreImpl()
    }

    private inner class DnevnikRuMarkStoreImpl :
        DnevnikRuMarkStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "DnevnikRuMarkStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = DnevnikRuMarkReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}