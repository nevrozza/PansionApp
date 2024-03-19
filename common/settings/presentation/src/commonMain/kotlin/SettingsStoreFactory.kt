import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import SettingsStore.Message

class SettingsStoreFactory(
    private val storeFactory: StoreFactory,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) {

    fun create(): SettingsStore {
        return SettingsStoreImpl()
    }

    private inner class SettingsStoreImpl :
        SettingsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "SettingsStore",
            initialState = State(
                login = authRepository.fetchLogin(),
                themeTint = settingsRepository.fetchTint(),
                color = settingsRepository.fetchColor(),
                language = settingsRepository.fetchLanguage()
            ),
            executorFactory = { SettingsExecutor(
                settingsRepository = settingsRepository,
                authRepository = authRepository
            ) },
            reducer = SettingsReducer
        )
}