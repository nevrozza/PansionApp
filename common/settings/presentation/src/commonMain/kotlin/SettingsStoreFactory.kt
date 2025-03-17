
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class SettingsStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: SettingsExecutor
) {

    fun create(): SettingsStore {
        return SettingsStoreImpl()
    }

    private inner class SettingsStoreImpl :
        SettingsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "SettingsStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = SettingsReducer
        )
}