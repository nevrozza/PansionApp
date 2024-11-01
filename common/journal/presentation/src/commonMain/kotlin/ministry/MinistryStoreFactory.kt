package ministry

import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import ministry.MinistryStore.Intent
import ministry.MinistryStore.Label
import ministry.MinistryStore.State

class MinistryStoreFactory(
    private val storeFactory: StoreFactory,
    private val nInterface: NetworkInterface,
    private val journalRepository: JournalRepository,

    private val ds1ListComponent: ListComponent,
    private val ds2ListComponent: ListComponent,
    private val ds3DialogComponent: CAlertDialogComponent,
    private val nUploadInterface: NetworkInterface
) {

    fun create(): MinistryStore {
        return MinistryStoreImpl()
    }

    private inner class MinistryStoreImpl :
        MinistryStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "MinistryStore",
            initialState = MinistryStore.State(),
            executorFactory = { MinistryExecutor(
                nInterface = nInterface,
                journalRepository = journalRepository,
                ds1ListComponent = ds1ListComponent,
                ds2ListComponent = ds2ListComponent,
                ds3DialogComponent = ds3DialogComponent,
                nUploadInterface = nUploadInterface
            ) },
            reducer = MinistryReducer
        )
}