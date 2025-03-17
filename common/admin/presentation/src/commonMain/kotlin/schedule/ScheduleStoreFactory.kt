package schedule

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import schedule.ScheduleStore.Intent
import schedule.ScheduleStore.Label
import schedule.ScheduleStore.State

class ScheduleStoreFactory(
    private val storeFactory: StoreFactory,
    private val state: State,
    private val executor: ScheduleExecutor
) {

    fun create(): ScheduleStore {
        return ScheduleStoreImpl()
    }

    private inner class ScheduleStoreImpl :
        ScheduleStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "ScheduleStore",
            initialState = state,
            executorFactory = ::executor,
            reducer = ScheduleReducer,
            bootstrapper = SimpleBootstrapper(Unit)
        )
}