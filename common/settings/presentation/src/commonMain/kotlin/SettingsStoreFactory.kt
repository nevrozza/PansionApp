
import SettingsStore.Intent
import SettingsStore.Label
import SettingsStore.State
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface

class SettingsStoreFactory(
    private val storeFactory: StoreFactory,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val nDevicesInterface: NetworkInterface,
    private val changeLoginDialog: CAlertDialogComponent
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
                isMarkTableDefault = settingsRepository.fetchIsMarkTable(),
                isPlusDsStupsEnabled = settingsRepository.fetchIsShowingPlusDS()
            ),
            executorFactory = { SettingsExecutor(
                settingsRepository = settingsRepository,
                authRepository = authRepository,
                nDevicesInterface = nDevicesInterface,
                changeLoginDialog = changeLoginDialog
            ) },
            reducer = SettingsReducer
        )
}