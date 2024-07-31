import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import di.Inject

class SettingsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {

    private val settingsRepository: SettingsRepository = Inject.instance()
    private val authRepository: AuthRepository = Inject.instance()

    private val colorModeListDialogComponentName = "ColorModeSettingsListDialogComponentName"

    val colorModeListComponent = ListComponent(
        componentContext = childContext(colorModeListDialogComponentName + "CONTEXT"),
        storeFactory = storeFactory,
        name = colorModeListDialogComponentName,
        onItemClick = {
            onChangeColorModeClick(it.id)
        }
    )


    private fun onChangeColorModeClick(id: String) {
        onEvent(SettingsStore.Intent.ChangeColorMode(id))
        colorModeListComponent.onEvent(ListDialogStore.Intent.HideDialog)
    }

    init {
        colorModeListComponent.onEvent(ListDialogStore.Intent.InitList(
            listOf(
                ListItem(
                    id = "0",
                    text = colorModes["0"].toString()
                ),
                ListItem(
                    id = "1",
                    text = colorModes["1"].toString()
                ),
                ListItem(
                    id = "2",
                    text = colorModes["2"].toString()
                ),
                ListItem(
                    id = "3",
                    text = colorModes["3"].toString()
                ),
            )
        ))
    }


    val quitDialogComponent = CAlertDialogComponent(
        componentContext,
        storeFactory,
        "accountQuitDialogComponent",
        onAcceptClick = {
            onEvent(SettingsStore.Intent.ClickOnQuit)
            onOutput(Output.GoToZero)
        }
    )

//    private fun onQuitDialogDeclineClick() {
//        quitDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
//    }


    private val settingsStore =
        instanceKeeper.getStore {
            SettingsStoreFactory(
                storeFactory = storeFactory,
                settingsRepository = settingsRepository,
                authRepository = authRepository,
                colorModeListComponent = colorModeListComponent
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
        data object Back : Output()
        data object GoToZero : Output()
    }
}

val colorModes = mapOf("0" to "Монохромный 1", "1" to "Монохромный 2", "2" to "Полный Монохром", "3" to "Цветной")