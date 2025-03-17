package dnevnikRuMarks

import SettingsRepository
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import di.Inject
import studentReportDialog.StudentReportComponent


class DnevnikRuMarksComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    private val studentLogin: String
) : ComponentContext by componentContext, DefaultMVIComponent<DnevnikRuMarkStore.Intent, DnevnikRuMarkStore.State, DnevnikRuMarkStore.Label> {
    val nInterface =
        NetworkInterface(componentContext, storeFactory, "DnevnikRuMarksComponent")


    val studentReportDialog = StudentReportComponent(
        componentContext = childContext("DnevnikRuMarksComponentDIALOGCONTEXT"),
        storeFactory = storeFactory
    )



    val stupsDialogComponent = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "StupsDialogComponentIntDnevnikRuMarks",
        {}
    )

    val settingsRepository = Inject.instance<SettingsRepository>()

    override val store =
        instanceKeeper.getStore(key = "dnevnikRuMark/$studentLogin") {
            DnevnikRuMarkStoreFactory(
                storeFactory = storeFactory,
                state = DnevnikRuMarkStore.State(
                    studentLogin = studentLogin,
                    isTableView = settingsRepository.fetchIsMarkTable()
                ),
                executor = DnevnikRuMarkExecutor(
                    nInterface = nInterface,
                    stupsDialogComponent = stupsDialogComponent,
                    studentReportDialog = studentReportDialog
                )
            ).create()
        }


    fun onOutput(output: Output) {
        output(output)
    }



    sealed class Output {
        data object Back : Output()

    }
}