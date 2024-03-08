package activation

import AuthRepository
import SettingsRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import activation.ActivationStore.Intent
import activation.ActivationStore.Step
import activation.ActivationStore.State

class ActivationStoreFactory(
    private val storeFactory: StoreFactory,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) {

    fun create(): ActivationStore {
        return ActivationStoreImpl()
    }

    private inner class ActivationStoreImpl :
        ActivationStore,
        Store<Intent, State, Nothing> by storeFactory.create(
            name = "ActivationStore",
            initialState = State(
                themeTint = settingsRepository.fetchTint(),
                language = settingsRepository.fetchLanguage(),
                color = settingsRepository.fetchColor()
            ),
            executorFactory = {
                ActivationExecutor(
                    settingsRepository = settingsRepository,
                    authRepository = authRepository
                )
            },
            reducer = ActivationReducer
        )
}