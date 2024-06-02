package groups.subjects

import AdminRepository
import MainRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import components.cAlertDialog.CAlertDialogComponent
import components.cBottomSheet.CBottomSheetComponent
import groups.subjects.SubjectsStore.Intent
import groups.subjects.SubjectsStore.Label
import groups.subjects.SubjectsStore.State

class SubjectsStoreFactory(
    private val storeFactory: StoreFactory,
    private val mainRepository: MainRepository,
    private val nGroupInterface: NetworkInterface,
    private val adminRepository: AdminRepository,
    private val nSubjectsInterface: NetworkInterface,
    private val updateSubjects: () -> Unit,
    private val cSubjectDialog: CAlertDialogComponent,
    private val cGroupBottomSheet: CBottomSheetComponent
) {

    fun create(): SubjectsStore {
        return SubjectsStoreImpl()
    }

    private inner class SubjectsStoreImpl :
        SubjectsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "SubjectsStore",
            initialState = SubjectsStore.State(),
            executorFactory = { SubjectsExecutor(
                adminRepository = adminRepository,
                nSubjectsInterface = nSubjectsInterface,
                updateSubjects = {updateSubjects()},
                cSubjectDialog = cSubjectDialog,
                cGroupBottomSheet = cGroupBottomSheet,
                mainRepository = mainRepository,
                nGroupInterface = nGroupInterface
            ) },
            reducer = SubjectsReducer
        )
}