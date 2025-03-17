package calendar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import decompose.getChildContext

class CalendarComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<CalendarStore.Intent, CalendarStore.State, CalendarStore.Label> {

    private val calendarComponentNInterfaceName =
        "calendarComponentNInterface"

    val nInterface = NetworkInterface(
        getChildContext(calendarComponentNInterfaceName),
        storeFactory,
        calendarComponentNInterfaceName
    )


    override val store =
        instanceKeeper.getStore {
            CalendarStoreFactory(
                storeFactory = storeFactory,
                executor = CalendarExecutor(
                    nInterface = nInterface
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