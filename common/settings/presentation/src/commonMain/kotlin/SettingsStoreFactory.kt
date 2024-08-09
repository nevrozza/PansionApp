import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import SettingsStore.Message
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface

class SettingsStoreFactory(
    private val storeFactory: StoreFactory,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val colorModeListComponent: ListComponent,
    private val nDevicesInterface: NetworkInterface
) {

    fun create(): SettingsStore {
        return SettingsStoreImpl()
    }

    private inner class SettingsStoreImpl :
        SettingsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "SettingsStore",
            initialState = State(
                login = authRepository.fetchLogin()
            ),
            executorFactory = { SettingsExecutor(
                settingsRepository = settingsRepository,
                authRepository = authRepository,
                colorModeListComponent = colorModeListComponent,
                nDevicesInterface = nDevicesInterface
            ) },
            reducer = SettingsReducer
        )
}