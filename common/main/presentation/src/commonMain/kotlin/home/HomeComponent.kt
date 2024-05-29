package home

import AuthRepository
import MainRepository
import admin.AdminComponent
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.cAlertDialog.CAlertDialogComponent
import components.networkInterface.NetworkInterface
import di.Inject
import journal.JournalComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class HomeComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    val journalComponent: JournalComponent? = null,
    private val output: (Output) -> Unit
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
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

    private val authRepository: AuthRepository = Inject.instance()
    private val mainRepository: MainRepository = Inject.instance()
    private val homeStore =
        instanceKeeper.getStore {
            HomeStoreFactory(
                storeFactory = storeFactory,
                authRepository = authRepository,
                mainRepository = mainRepository,
                quickTabNInterface = quickTabNInterface,
                teacherNInterface = teacherNInterface,
                gradesNInterface = gradesNInterface,
                scheduleNInterface = scheduleNInterface,
                journalComponent = journalComponent
            ).create()
        }

    val model = homeStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HomeStore.State> = homeStore.stateFlow

    fun getLogin() : String {
        return model.value.login
    }

    fun onEvent(event: HomeStore.Intent) {
        homeStore.accept(event)
    }

    init {
        onEvent(HomeStore.Intent.Init)
        //.Init(
        //            avatarId = authRepository.fetchAvatarId(),
        //            login = authRepository.fetchLogin(),
        //            name = authRepository.fetchName(),
        //            surname = authRepository.fetchSurname(),
        //            praname = authRepository.fetchPraname()
        //
    }

    fun onOutput(output: Output) {
        output(output)
    }

    sealed class Output {
        data object NavigateToSettings : Output()

        data class NavigateToDnevnikRuMarks(val studentLogin: String) : Output()
        data class NavigateToDetailedStups(val studentLogin: String, val reason: Int) : Output()
        data class NavigateToAllGroupMarks(val subjectId: Int, val subjectName: String, val groupId: Int, val groupName: String) : Output()
    }
}