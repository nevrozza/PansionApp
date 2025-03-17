package studentReportDialog

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cBottomSheet.CBottomSheetComponent
import studentReportDialog.StudentReportDialogStore.Intent
import studentReportDialog.StudentReportDialogStore.Label
import studentReportDialog.StudentReportDialogStore.State

class StudentReportDialogStoreFactory(
    private val storeFactory: StoreFactory,
    private val dialog: CBottomSheetComponent,
) {

    fun create(): StudentReportDialogStore {
        return StudentReportDialogStoreImpl()
    }

    private inner class StudentReportDialogStoreImpl :
        StudentReportDialogStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "StudentReportDialogStore",
            initialState = State(),
            executorFactory = { StudentReportDialogExecutor(
                dialog = dialog
            ) },
            reducer = StudentReportDialogReducer
        )
}