package mentoring

import FIO
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import root.RootComponent

//data class JournalComponentData(
//    val header: ReportHeader
//)

class MentoringComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<MentoringStore.Intent, MentoringStore.State, MentoringStore.Label> {

    private val nInterfaceName = "MentoringComponentNInterface"
    private val nPreAttendanceInterfaceName = "MentoringPreAttendanceComponentNInterface"

    val nInterface = NetworkInterface(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = nInterfaceName
    )

    val nPreAttendanceInterface = NetworkInterface(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = nPreAttendanceInterfaceName
    )
    override val store =
        instanceKeeper.getStore {
            MentoringStoreFactory(
                storeFactory = storeFactory,
                executor = MentoringExecutor(
                    nInterface = nInterface,
                    nPreAttendance = nPreAttendanceInterface
                )
            ).create()
        }


    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data class CreateSecondView(val login: String, val fio: FIO, val avatarId: Int, val config: RootComponent.Config) : Output()
        data object NavigateToAchievements : Output()
    }
}