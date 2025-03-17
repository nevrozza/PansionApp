package school

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import school.SchoolStore.Intent
import school.SchoolStore.Label
import school.SchoolStore.State

class SchoolStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: SchoolExecutor
) {

    fun create(): SchoolStore {
        return SchoolStoreImpl()
    }

    private inner class SchoolStoreImpl :
        SchoolStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "SchoolStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = SchoolReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}