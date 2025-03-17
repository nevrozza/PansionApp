package home

import FIO
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import home.HomeStore.State
import journal.JournalComponent
import school.SchoolComponent
import school.SchoolStore
import server.Moderation
import studentReportDialog.StudentReportComponent


class HomeComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val journalComponent: JournalComponent? = null,
    val schoolComponent: SchoolComponent,
    private val avatarId: Int,
    private val login: String,
    private val name: String,
    private val surname: String,
    private val praname: String,
    private val role: String,
    private val isParent: Boolean,
    private val moderation: String,
    val onBackButtonPress: (() -> Unit)? = null,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext, DefaultMVIComponent<HomeStore.Intent, State, HomeStore.Label> {
    val reportsDialog = CAlertDialogComponent(
        componentContext,
        storeFactory,
        name = "ReportsInHomeDialogComss",
        onAcceptClick = {}
    )


    val quickTabNInterface = NetworkInterface(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = "QuickTabNetworkkInterface"
    )

    val gradesNInterface = NetworkInterface(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = "GradesssNetworkkInterface"
    )

    val teacherNInterface = NetworkInterface(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = "TeacherNetworkkInterface"
    )
    val scheduleNInterface = NetworkInterface(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = "ScheduleNetworkkInterface"
    )

    val studentReportDialog = StudentReportComponent(
        componentContext = componentContext,
        storeFactory = storeFactory
    )

    override val store =
        instanceKeeper.getStore {
            HomeStoreFactory(
                storeFactory = storeFactory,
                state = State(
                    avatarId = avatarId,
                    login = login,
                    name = name,
                    surname = surname,
                    praname = praname,
                    role = role,
                    isParent = isParent,
                    isMentor = moderation in listOf(Moderation.MENTOR, Moderation.BOTH),
                    isModer = moderation in listOf(Moderation.MODERATOR, Moderation.BOTH)
                ),
                executor = HomeExecutor(
                    quickTabNInterface = quickTabNInterface,
                    teacherNInterface = teacherNInterface,
                    gradesNInterface = gradesNInterface,
                    scheduleNInterface = scheduleNInterface,
                    journalComponent = journalComponent
                )
            ).create()
        }


    fun onRefreshClick() {
        onEvent(HomeStore.Intent.Init)
        schoolComponent.onEvent(SchoolStore.Intent.RefreshOnlyDuty)
    }
    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data class NavigateToProfile(val studentLogin: String, val fio: FIO, val avatarId: Int) : Output()
        data object NavigateToSettings : Output()

        data class NavigateToTasks(val studentLogin: String, val avatarId: Int, val name: String) : Output()

        data class NavigateToDnevnikRuMarks(val studentLogin: String) : Output()
        data class NavigateToDetailedStups(val studentLogin: String, val reason: Int, val name: String, val avatarId: Int) : Output()
        data class NavigateToAllGroupMarks(val subjectId: Int, val subjectName: String, val groupId: Int, val groupName: String, val teacherLogin: String) : Output()
        data class NavigateToStudentLines(val studentLogin: String) : Output()

        data class NavigateToChildren(val studentLogin: String, val fio: FIO, val avatarId: Int) : Output()

        data object NavigateToSchool : Output()
    }
}