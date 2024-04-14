package journal

import MainRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import journal.JournalStore.Intent
import journal.JournalStore.Label
import journal.JournalStore.State

class JournalStoreFactory(
    private val storeFactory: StoreFactory,
    private val mainRepository: MainRepository,
    private val groupListComponent: ListComponent,
    private val studentsInGroupCAlertDialogComponent: CAlertDialogComponent,
    private val nInterface: NetworkInterface,
    private val nOpenReportInterface: NetworkInterface
) {

    fun create(): JournalStore {
        return JournalStoreImpl()
    }

    private inner class JournalStoreImpl :
        JournalStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "JournalStore",
            initialState = JournalStore.State(),
            executorFactory = { JournalExecutor(
                mainRepository = mainRepository,
                groupListComponent = groupListComponent,
                studentsInGroupCAlertDialogComponent = studentsInGroupCAlertDialogComponent,
                nInterface = nInterface,
                nOpenReportInterface = nOpenReportInterface
            ) },
            reducer = JournalReducer
        )
}