import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import di.Inject

class SettingsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {

    private val settingsRepository: SettingsRepository = Inject.instance()
    private val authRepository: AuthRepository = Inject.instance()
    val quitDialogComponent = CAlertDialogComponent(
        componentContext,
        storeFactory,
        "accountQuitDialogComponent",
        onAcceptClick = {
            onEvent(SettingsStore.Intent.ClickOnQuit)
            onOutput(Output.GoToZero)
        }
//        onDeclineClick = {
//            onQuitDialogDeclineClick()
//        }
    )

//    private fun onQuitDialogDeclineClick() {
//        quitDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
//    }


    private val settingsStore =
        instanceKeeper.getStore {
            SettingsStoreFactory(
                storeFactory = storeFactory,
                settingsRepository = settingsRepository,
                authRepository = authRepository
            ).create()
        }

    val model = settingsStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: SettingsStore.Intent) {
        settingsStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object BackToHome : Output()
        data object GoToZero : Output()
    }
}