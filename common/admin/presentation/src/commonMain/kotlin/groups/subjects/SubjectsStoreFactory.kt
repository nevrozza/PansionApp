package groups.subjects

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import groups.subjects.SubjectsStore.Intent
import groups.subjects.SubjectsStore.Label
import groups.subjects.SubjectsStore.State

class SubjectsStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: SubjectsExecutor
) {

    fun create(): SubjectsStore {
        return SubjectsStoreImpl()
    }

    private inner class SubjectsStoreImpl :
        SubjectsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "SubjectsStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = SubjectsReducer
        )
}