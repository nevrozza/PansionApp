package calendar

import AdminRepository
import asValue
import cabinets.CabinetsStore
import cabinets.CabinetsStoreFactory
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import di.Inject

class CalendarComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    val nInterface = NetworkInterface(
        componentContext,
        storeFactory,
        "calendarComponentNInterface"
    )

    private val adminRepository: AdminRepository = Inject.instance()


    private val calendarStore =
        instanceKeeper.getStore {
            CalendarStoreFactory(
                storeFactory = storeFactory,
                adminRepository = adminRepository,
                nInterface = nInterface
            ).create()
        }

    private val backCallback = BackCallback {
        onOutput(Output.BackToAdmin)
    }


    init {
        backHandler.register(backCallback)
        onEvent(CalendarStore.Intent.Init)
    }

    val model = calendarStore.asValue()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val state: StateFlow<UsersStore.State> = usersStore.stateFlow

    fun onEvent(event: CalendarStore.Intent) {
        calendarStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object BackToAdmin : Output()
    }
}