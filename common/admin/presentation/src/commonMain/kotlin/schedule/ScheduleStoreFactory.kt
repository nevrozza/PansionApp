package schedule

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListComponent
import components.mpChose.MpChoseComponent
import components.networkInterface.NetworkInterface
import schedule.ScheduleStore.Intent
import schedule.ScheduleStore.Label
import schedule.ScheduleStore.State

class ScheduleStoreFactory(
    private val storeFactory: StoreFactory,
    private val adminRepository: AdminRepository,

    private val login: String,
    private val nInterface: NetworkInterface,
    private val mpCreateItem: MpChoseComponent,
    private val mpEditItem: MpChoseComponent,
    private val listCreateTeacher: ListComponent
) {

    fun create(): ScheduleStore {
        return ScheduleStoreImpl()
    }

    private inner class ScheduleStoreImpl :
        ScheduleStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "ScheduleStore",
            initialState = ScheduleStore.State(
                login = login
            ),
            executorFactory = { ScheduleExecutor(
                adminRepository = adminRepository,
                nInterface = nInterface,
                mpCreateItem = mpCreateItem,
                mpEditItem = mpEditItem,
                listCreateTeacher = listCreateTeacher
            ) },
            reducer = ScheduleReducer
        )
}