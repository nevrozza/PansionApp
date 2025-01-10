package rating

import AuthRepository
import FIO
import MainRepository
import asValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import di.Inject
import home.HomeComponent
import home.HomeComponent.Output
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow



class RatingComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    private val avatarId: Int,
    private val login: String,
    private val fio: FIO
) : ComponentContext by componentContext {
    //    private val settingsRepository: SettingsRepository = Inject.instance()
    val nInterface = NetworkInterface(
        componentContext = componentContext,
        storeFactory = storeFactory,
        name = "RatingNetworkkInterface"
    )

    val subjectsListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "RatingSubjectsListComponent",
        onItemClick = {onSubjectItemClick(it.id.toInt())}
    )
    val formsListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "RatingFormNumsListComponent",
        onItemClick = {onFormItemClick(it.id.toInt())}
    )
    val weekListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "RatingWeekListComponent",
        onItemClick = {onPeriodItemClick(it.id)}
    )
    val moduleListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "RatingModuleListComponent",
        onItemClick = {onPeriodItemClick(it.id)}
    )
    val periodListComponent = ListComponent(
        componentContext,
        storeFactory,
        name = "RatingPeriodListComponent",
        onItemClick = {onPeriodItemClick(it.id)}
    )

    private fun onFormItemClick(id: Int) {
        onEvent(RatingStore.Intent.ClickOnForm(id))
        formsListComponent.onEvent(ListDialogStore.Intent.HideDialog)
    }
    private fun onPeriodItemClick(id: String) {
        onEvent(RatingStore.Intent.ClickOnPeriod(id))
        listOf(weekListComponent, moduleListComponent, periodListComponent).forEach {
            it.onEvent(ListDialogStore.Intent.HideDialog)
        }
    }

    private fun onSubjectItemClick(id: Int) {
        onEvent(RatingStore.Intent.ClickOnSubject(id))
        subjectsListComponent.onEvent(ListDialogStore.Intent.HideDialog)
    }

    private val authRepository: AuthRepository = Inject.instance()
    private val mainRepository: MainRepository = Inject.instance()
    private val homeStore =
        instanceKeeper.getStore {
            RatingStoreFactory(
                storeFactory = storeFactory,
                authRepository = authRepository,
                mainRepository = mainRepository,
                nInterface = nInterface,
                subjectsListComponent = subjectsListComponent,
                avatarId = avatarId,
                login = login,
                fio = fio,
                weeksListComponent = weekListComponent,
                moduleListComponent = moduleListComponent,
                periodListComponent = periodListComponent
            ).create()
        }

    val model = homeStore.asValue()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<RatingStore.State> = homeStore.stateFlow

    fun getLogin() : String {
        return model.value.login
    }

    fun onEvent(event: RatingStore.Intent) {
        homeStore.accept(event)
    }

    init {
        formsListComponent.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(Pair(0, "Все"), Pair(1, "5-8 классы"), Pair(2, "9-11 классы")).map {
                    ListItem(
                        id = it.first.toString(),
                        text = it.second
                    )
                }
            )
        )
        periodListComponent.onEvent(
            ListDialogStore.Intent.InitList(
                listOf(Pair(PansionPeriod.Year, "За год")).map {
                    ListItem(
                        id = it.first.toStr(),
                        text = it.second
                    )
                }
            )
        )

        onEvent(RatingStore.Intent.Init)
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
        data object Back : Output()
        data class NavigateToProfile(val studentLogin: String, val fio: FIO, val avatarId: Int) : Output()
    }
}