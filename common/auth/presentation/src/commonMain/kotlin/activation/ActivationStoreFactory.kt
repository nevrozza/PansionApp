package activation

import activation.ActivationStore.Intent
import activation.ActivationStore.State
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class ActivationStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): ActivationStore {
        return ActivationStoreImpl()
    }

    private inner class ActivationStoreImpl :
        ActivationStore,
        Store<Intent, State, Nothing> by storeFactory.create(
            name = "ActivationStore",
            initialState = State(),
            executorFactory = {
                ActivationExecutor()
            },
            reducer = ActivationReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}