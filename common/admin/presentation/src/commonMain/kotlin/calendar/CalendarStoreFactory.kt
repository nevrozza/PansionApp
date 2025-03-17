package calendar

import calendar.CalendarStore.Intent
import calendar.CalendarStore.Label
import calendar.CalendarStore.State
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

class CalendarStoreFactory(
    private val storeFactory: StoreFactory,
    private val executor: CalendarExecutor
) {

    fun create(): CalendarStore {
        return CalendarStoreImpl()
    }

    private inner class CalendarStoreImpl :
        CalendarStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "CalendarStore",
            initialState = State(),
            executorFactory = ::executor,
            reducer = CalendarReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}