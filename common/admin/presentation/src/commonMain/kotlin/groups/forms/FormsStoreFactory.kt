package groups.forms

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import groups.forms.FormsStore.Intent
import groups.forms.FormsStore.Label
import groups.forms.FormsStore.State

class FormsStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: FormsExecutor
) {

    fun create(): FormsStore {
        return FormsStoreImpl()
    }

    private inner class FormsStoreImpl :
        FormsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "FormsStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = FormsReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}