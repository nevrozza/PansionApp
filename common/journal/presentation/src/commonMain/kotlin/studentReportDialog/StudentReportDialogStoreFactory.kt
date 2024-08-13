package studentReportDialog

import JournalRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import components.networkInterface.NetworkInterface
import studentReportDialog.StudentReportDialogStore.Intent
import studentReportDialog.StudentReportDialogStore.Label
import studentReportDialog.StudentReportDialogStore.State
import studentReportDialog.StudentReportDialogStore.Message

class StudentReportDialogStoreFactory(
    private val storeFactory: StoreFactory,
    private val journalRepository: JournalRepository,
    private val dialog: CBottomSheetComponent,
) {

    fun create(): StudentReportDialogStore {
        return StudentReportDialogStoreImpl()
    }

    private inner class StudentReportDialogStoreImpl :
        StudentReportDialogStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "StudentReportDialogStore",
            initialState = StudentReportDialogStore.State(),
            executorFactory = { StudentReportDialogExecutor(
                journalRepository = journalRepository,
                dialog = dialog
            ) },
            reducer = StudentReportDialogReducer
        )
}