import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import di.Inject
import view.FontTypes

class SettingsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {

    private val settingsRepository: SettingsRepository = Inject.instance()
    private val authRepository: AuthRepository = Inject.instance()

    private val colorModeListDialogComponentName = "ColorModeSettingsListDialogComponentName"
    private val fontTypeListDialogComponentName = "FontTypeSettingsListDialogComponentName"


    val changeLoginDialog = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "changeSecondLoginDialog",
        onAcceptClick = {
            onEvent(SettingsStore.Intent.SaveSecondLogin)
//            onEvent(SubjectsStore.Intent.CreateSubject)
        },
    )

    val colorModeListComponent = ListComponent(
        componentContext = childContext(colorModeListDialogComponentName + "CONTEXT"),
        storeFactory = storeFactory,
        name = colorModeListDialogComponentName,
        onItemClick = {
            onChangeColorModeClick(it.id)
        }
    )

    val fontTypeListComponent = ListComponent(
        componentContext = childContext(fontTypeListDialogComponentName + "CONTEXT"),
        storeFactory = storeFactory,
        name = fontTypeListDialogComponentName,
        onItemClick = {
//            onChangeColorModeClick(it.id)
        }
    )

    private val nDevicesInterfaceName = "nDevicesInterfaceName"

    val nDevicesInterface = NetworkInterface(
        childContext(nDevicesInterfaceName + "CONTEXT"),
        storeFactory = storeFactory,
        name = nDevicesInterfaceName
    )


    private fun onChangeColorModeClick(id: String) {
        onEvent(SettingsStore.Intent.ChangeColorMode(id))
        colorModeListComponent.onEvent(ListDialogStore.Intent.HideDialog)
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
                nDevicesInterface = nDevicesInterface,
                changeLoginDialog = changeLoginDialog
            ).create()
        }

    val model = settingsStore.asValue()
    val labels = settingsStore.labels

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

        data object GoToScanner : Output()
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

        fontTypeListComponent.onEvent(ListDialogStore.Intent.InitList(
            listOf(
                ListItem(FontTypes.Geologica.ordinal.toString(), "Geologica"),
//                ListItem(FontTypes.Cursive.ordinal.toString(), "Cursive"),
                ListItem(FontTypes.Default.ordinal.toString(), "Обычный"),
                ListItem(FontTypes.Monospace.ordinal.toString(), "Monospace"),
                ListItem(FontTypes.SansSerif.ordinal.toString(), "SansSerif"),
//                ListItem(FontTypes.Serif.ordinal.toString(), "Serif"),
            )
        ))

        onEvent(SettingsStore.Intent.FetchDevices)
    }
}

val colorModes = mapOf("0" to "Монохромный 1", "1" to "Монохромный 2", "2" to "Полный Монохром", "3" to "Цветной")