package groups.students

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import components.listDialog.ListComponent
import groups.students.StudentsStore.Intent
import groups.students.StudentsStore.Label
import groups.students.StudentsStore.State

class StudentsStoreFactory(
    private val storeFactory: StoreFactory,
    private val adminRepository: AdminRepository,
    private val formsListComponent: ListComponent,
    private val nStudentsInterface: NetworkInterface,
    private val nStudentGroupsInterface: NetworkInterface
) {

    fun create(): StudentsStore {
        return StudentsStoreImpl()
    }

    private inner class StudentsStoreImpl :
        StudentsStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "StudentsStore",
            initialState = StudentsStore.State(),
            executorFactory = { StudentsExecutor(
                adminRepository = adminRepository,
                formsListComponent = formsListComponent,
                nStudentsInterface = nStudentsInterface,
                nStudentGroupsInterface = nStudentGroupsInterface
            ) },
            reducer = StudentsReducer
        )
}