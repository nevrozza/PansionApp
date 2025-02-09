package journal

import AuthRepository
import MainRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListComponent
import components.networkInterface.NetworkInterface
import journal.JournalStore.Intent
import journal.JournalStore.Label
import journal.JournalStore.State
import server.Moderation

class JournalStoreFactory(
    private val storeFactory: StoreFactory,
    private val mainRepository: MainRepository,
    private val authRepository: AuthRepository,
    private val groupListComponent: ListComponent,
    private val studentsInGroupCAlertDialogComponent: CAlertDialogComponent,
    private val nInterface: NetworkInterface,
    private val nOpenReportInterface: NetworkInterface,
    private val fDateListComponent: ListComponent,
    private val fGroupListComponent: ListComponent,
    private val fTeachersListComponent: ListComponent,
    private val fStatusListComponent: ListComponent,
) {

    fun create(): JournalStore {
        return JournalStoreImpl()
    }

    private inner class JournalStoreImpl :
        JournalStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "JournalStore",
            initialState = State(
                isMentor = authRepository.fetchModeration() in listOf(Moderation.MENTOR, Moderation.BOTH),
                login = authRepository.fetchLogin()
            ),
            executorFactory = { JournalExecutor(
                mainRepository = mainRepository,
                groupListComponent = groupListComponent,
                studentsInGroupCAlertDialogComponent = studentsInGroupCAlertDialogComponent,
                nInterface = nInterface,
                nOpenReportInterface = nOpenReportInterface,
                fDateListComponent = fDateListComponent,
                fGroupListComponent = fGroupListComponent,
                fTeachersListComponent = fTeachersListComponent,
                fStatusListComponent = fStatusListComponent
            ) },
            reducer = JournalReducer
        )
}