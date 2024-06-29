package calendar

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import calendar.CalendarStore.Intent
import calendar.CalendarStore.Label
import calendar.CalendarStore.State
import calendar.CalendarStore.Message
import components.networkInterface.NetworkInterface

class CalendarStoreFactory(
    private val storeFactory: StoreFactory,
    private val adminRepository: AdminRepository,
    private val nInterface: NetworkInterface
) {

    fun create(): CalendarStore {
        return CalendarStoreImpl()
    }

    private inner class CalendarStoreImpl :
        CalendarStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "CalendarStore",
            initialState = State(),
            executorFactory = { CalendarExecutor(
                adminRepository = adminRepository,
                nInterface = nInterface
            ) },
            reducer = CalendarReducer
        )
}