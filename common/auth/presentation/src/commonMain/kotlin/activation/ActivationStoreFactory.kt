package activation

import AuthRepository
import activation.ActivationStore.Intent
import activation.ActivationStore.State
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class ActivationStoreFactory(
    private val storeFactory: StoreFactory,
    private val authRepository: AuthRepository
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
                ActivationExecutor(
                    authRepository = authRepository
                )
            },
            reducer = ActivationReducer
        )
}