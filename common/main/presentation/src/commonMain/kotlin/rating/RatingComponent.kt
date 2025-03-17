package rating

import FIO
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.listDialog.ListComponent
import components.listDialog.ListDialogStore
import components.listDialog.ListItem
import components.networkInterface.NetworkInterface
import decompose.DefaultMVIComponent
import rating.RatingStore.State


class RatingComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: (Output) -> Unit,
    private val avatarId: Int,
    private val login: String,
    private val fio: FIO
) : ComponentContext by componentContext, DefaultMVIComponent<RatingStore.Intent, State, RatingStore.Label> {

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
    val weeksListComponent = ListComponent(
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
        listOf(weeksListComponent, moduleListComponent, periodListComponent).forEach {
            it.onEvent(ListDialogStore.Intent.HideDialog)
        }
    }

    private fun onSubjectItemClick(id: Int) {
        onEvent(RatingStore.Intent.ClickOnSubject(id))
        subjectsListComponent.onEvent(ListDialogStore.Intent.HideDialog)
    }

    override val store =
        instanceKeeper.getStore {
            RatingStoreFactory(
                storeFactory = storeFactory,
                state = State(
                    avatarId = avatarId,
                    fio = fio,
                    login = login
                ),
                executor = RatingExecutor(
                    nInterface = nInterface,
                    subjectsListComponent = subjectsListComponent,
                    weeksListComponent = weeksListComponent,
                    moduleListComponent = moduleListComponent,
                    periodListComponent = periodListComponent
                )
            ).create()
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