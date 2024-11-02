package mentoring

import FIO
import MainRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import di.Inject
import profile.ProfileStore
import root.RootComponent
import root.RootComponent.Child
import root.RootComponent.Config

//data class JournalComponentData(
//    val header: ReportHeader
//)

class MentoringComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {

    val mainRepository: MainRepository = Inject.instance()

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
    private val mentoringStore =
        instanceKeeper.getStore {
            MentoringStoreFactory(
                storeFactory = storeFactory,
                mainRepository = mainRepository,
                nInterface = nInterface,
                nPreAttendance = nPreAttendanceInterface
            ).create()
        }

    val model = mentoringStore.asValue()


    init {
        onEvent(MentoringStore.Intent.FetchStudents)
    }

    fun onEvent(event: MentoringStore.Intent) {
        mentoringStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data class CreateSecondView(val login: String, val fio: FIO, val avatarId: Int, val config: RootComponent.Config) : Output()
        data object NavigateToAchievements : Output()
    }
}