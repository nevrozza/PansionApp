package journal

import MainRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import journal.JournalStore.Intent
import journal.JournalStore.Label
import journal.JournalStore.State

class JournalStoreFactory(
    private val storeFactory: StoreFactory,
    private val mainRepository: MainRepository,
    private val groupListComponent: ListComponent,
    private val studentsInGroupCAlertDialogComponent: CAlertDialogComponent,
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
                studentsInGroupCAlertDialogComponent = studentsInGroupCAlertDialogComponent
            ) },
            reducer = JournalReducer
        )
}