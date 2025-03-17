package allGroupMarks

import allGroupMarks.AllGroupMarksStore.Intent
import allGroupMarks.AllGroupMarksStore.Label
import allGroupMarks.AllGroupMarksStore.State
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class AllGroupMarksStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: AllGroupMarksExecutor,
    private val state: State
) {

    fun create(): AllGroupMarksStore {
        return AllGroupMarksStoreImpl()
    }

    private inner class AllGroupMarksStoreImpl :
        AllGroupMarksStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "AllGroupMarksStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = AllGroupMarksReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}