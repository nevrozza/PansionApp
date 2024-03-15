package journal

import MainRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cAlertDialog.CAlertDialogComponent
import components.cAlertDialog.CAlertDialogStore
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class JournalComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
//    private val authRepository: AuthRepository = Inject.instance()
    private val mainRepository: MainRepository = Inject.instance()
    val groupListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "groupListInMainJournal",
        onItemClick = {
//            onEvent(JournalStore.Intent.CreateUserForm(it.id))
            onEvent(JournalStore.Intent.OnGroupClicked(it.id))
        })
    val studentsInGroupCAlertDialogComponent = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "studentsInGroupListCAlertDialogInMainJournal",
        onAcceptClick = {
            createReport()
        },
        onDeclineClick = {
            //some magic..
            groupListComponent.onEvent(ListDialogStore.Intent.ShowDialog(65566556f, 65566556f))
            hideStudentAlarm()
        }
    )

    private fun hideStudentAlarm() {
        studentsInGroupCAlertDialogComponent.onEvent(CAlertDialogStore.Intent.HideDialog)
    }

    private fun createReport() {
        hideStudentAlarm()
        onOutput(Output.NavigateToLessonReport(model.value.currentGroupId))
    }

    private val journalStore =
        instanceKeeper.getStore {
            JournalStoreFactory(
                storeFactory = storeFactory,
                mainRepository = mainRepository,
                groupListComponent = groupListComponent,
                studentsInGroupCAlertDialogComponent = studentsInGroupCAlertDialogComponent
//                authRepository = authRepository
            ).create()
        }

    val model = journalStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<JournalStore.State> = journalStore.stateFlow


    init {
        onEvent(JournalStore.Intent.Init)
    }

    fun onEvent(event: JournalStore.Intent) {
        journalStore.accept(event)
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data class NavigateToLessonReport(val lessonReportId: Int) : Output()
    }
}