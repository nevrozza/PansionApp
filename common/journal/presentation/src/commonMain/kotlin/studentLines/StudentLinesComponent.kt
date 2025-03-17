package studentLines

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import studentReportDialog.StudentReportComponent

class StudentLinesComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    login: String,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<StudentLinesStore.Intent, StudentLinesStore.State, StudentLinesStore.Label> {
    private val nInterfaceName = "StudentLinesInterfaceName"

    val studentReportDialog = StudentReportComponent(
        componentContext = childContext(nInterfaceName + "DIALOGCONTEXT"),
        storeFactory = storeFactory
    )

    val nInterface =
        NetworkInterface(childContext(nInterfaceName + "CONTEXT"), storeFactory, nInterfaceName)

    override val store =
        instanceKeeper.getStore {
            StudentLinesStoreFactory(
                storeFactory = storeFactory,
                login = login,
                nInterface = nInterface
            ).create()
        }

    fun onOutput(output: Output) {
        output(output)
    }


    sealed class Output {
        data object Back : Output()
    }
}